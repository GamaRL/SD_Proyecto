package mx.unam.fi.distributed.messages.handler;

import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.server.IServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//@Service
@Slf4j
public class NodeHandler implements IServer {

    private final int port = 5001;


    private String processRequest(ServerSocket serverSocket) {
        String message = null;

        try (
                Socket socket = serverSocket.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            message = in.readUTF();
            out.writeUTF("ACCEPTED");
        } catch(Exception e) {
            log.error(e.getMessage());
        }

        return message;
    }

    @Override
    public void start() {

        Thread t = new Thread(() -> {
            try(ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    var response = processRequest(serverSocket);

                    log.info("New message: '{}'", response);
                }
            } catch(Exception e) {
                log.error(e.getMessage());
            }
        });

        t.start();
    }
}
