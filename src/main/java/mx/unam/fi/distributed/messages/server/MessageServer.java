package mx.unam.fi.distributed.messages.server;

import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.MessagesApplication;
import mx.unam.fi.distributed.messages.messages.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Service
@Slf4j
public class MessageServer implements IMessageServer {

    private static final int MAX_REQUESTS = 10;
    private ServerSocket socket;
    private boolean isAlive = false;
    private final int port;
    private final BlockingQueue<Socket> pendingRequests;

    public MessageServer(
            @Value("${app.server.port}") int port) {
        this.pendingRequests = new LinkedBlockingQueue<>(MAX_REQUESTS);
        this.port = port;
    }

    @Override
    public void listen() throws IOException {
        socket = new ServerSocket(port);
        isAlive = true;
        log.info("Message server is listening on port {}", port);
    }

    @Override
    public void kill() throws IOException {
        isAlive = false;
        socket.close();
        log.info("Message server is down");
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }


    @Override
    public void run() {

        for (int i = 1; i <= MAX_REQUESTS; i++) {
            Thread p = new Thread(new RequestHandler(pendingRequests, MessagesApplication.incomingMessages), String.format("ReqHandler-%d", i));
            p.start();
        }

        try {
            while (isAlive()) {
                System.out.println(pendingRequests.size());
                pendingRequests.put(socket.accept());
                System.out.println(pendingRequests.size());
                log.info("Processing requests");
            }
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }
}
