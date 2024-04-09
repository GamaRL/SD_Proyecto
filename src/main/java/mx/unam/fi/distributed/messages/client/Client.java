package mx.unam.fi.distributed.messages.client;

import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.node.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

@Slf4j
public class Client implements IClient {
    @Override
    public Optional<Message> sendMessage(Node destination, Message message) {
        Message response;
        try (
                Socket socket = new Socket(destination.host(), destination.port());
                var in = new ObjectInputStream(socket.getInputStream());
                var out = new ObjectOutputStream(socket.getOutputStream())
        ) {

            log.info("Sending a message: {}", message);

            out.writeObject(message);
            response = (Message)in.readObject();

            log.info("Response from server was: {}", response);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(response);
    }
}
