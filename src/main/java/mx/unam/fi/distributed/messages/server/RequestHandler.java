package mx.unam.fi.distributed.messages.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
@Slf4j
public class RequestHandler implements Runnable {
    private final BlockingQueue<Socket> pendingRequests;
    private final Semaphore semaphore;

    private String processRequest(Socket socket) throws IOException {
        String message;


        var in = new DataInputStream(socket.getInputStream());
        var out = new DataOutputStream(socket.getOutputStream());

        message = in.readUTF();
        out.writeUTF("ACCEPTED");


        return message;
    }

    @Override
    public void run() {
        String response;
        try {
            semaphore.acquire();

            try (
                    var socket = pendingRequests.take()
            ) {
                response = processRequest(socket);
            }

            log.info("Nes message {}", response);
            semaphore.release();

            semaphore.release();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
