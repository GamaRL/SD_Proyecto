package mx.unam.fi.distributed.messages.server;

import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.MessagesApplication;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.storage.MessageRepository;
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
    private final MessageRepository messageRepository;

    @Value("${HOST}")
    private String HOST;

    public MessageServer(
            @Value("${app.server.port}") int port,
            MessageRepository messageRepository) {
        this.pendingRequests = new LinkedBlockingQueue<>(MAX_REQUESTS);
        this.port = port;
        this.messageRepository = messageRepository;
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
            Thread p = new Thread(new RequestHandler(HOST, pendingRequests, MessagesApplication.incomingMessages, this.messageRepository), String.format("ReqHandler-%d", i));
            p.start();
        }

        try {
            while (isAlive()) {
                pendingRequests.put(socket.accept());
                log.info("Processing requests");
            }
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }
}
