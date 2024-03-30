package mx.unam.fi.distributed.messages.client;

import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.node.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Optional;

@Slf4j
public class Client implements IClient {
    @Override
    public Optional<String> sendMessage(Node destination, String message) {
        String response;
        try (Socket socket = new Socket(destination.host(), destination.port())) {

            log.info("Sending a message: {}", message);
            var in = new DataInputStream(socket.getInputStream());
            var out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(message);
            response = in.readUTF();
            log.info("Response from server was: {}", response);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.of(response);
    }
}
