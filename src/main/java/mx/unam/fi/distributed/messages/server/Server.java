package mx.unam.fi.distributed.messages.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Service
@Slf4j
public class Server implements IServer {

    private final int port;
    private final Semaphore semaphore = new Semaphore(0);
    private final BlockingQueue<Socket> pendingRequests = new LinkedBlockingQueue<>(10);

    public Server(
            @Value("${app.server.port}") int port) {
        this.port = port;
    }

    @Override
    public void start() {

        Thread t = new Thread(() -> {
            try(ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    pendingRequests.put(serverSocket.accept());
                    semaphore.release();
                }
            } catch(Exception e) {
                log.error(e.getMessage());
            }
        });

        for (int i = 1; i <= 5; i++) {
            Thread p = new Thread(new RequestHandler(pendingRequests, semaphore), String.format("ReqHandler-%d", i));
            p.start();
        }

        t.start();
    }
}
