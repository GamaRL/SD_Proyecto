package mx.unam.fi.distributed.messages.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.listeners.NoReachableNodeEvent;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.node.Node;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class Client implements IClient {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public synchronized Optional<Message> sendMessage(Node destination, Message message) {
        Message response = null;
        try (
                Socket socket = new Socket()
        ) {
            socket.connect(new InetSocketAddress(destination.host(), destination.port()), 1000);
            var in = new ObjectInputStream(socket.getInputStream());
            var out = new ObjectOutputStream(socket.getOutputStream());
            socket.setSoTimeout(1000);
            log.info("Sending a message: {}", message);

            out.writeObject(message);
            response = (Message)in.readObject();

            log.info("Response from server was: {}", response);

            in.close();
            out.close();

        } catch (SocketTimeoutException | SocketException | EOFException e) {
            eventPublisher.publishEvent(new NoReachableNodeEvent(this, destination));

            log.info("REMOVING {} BECAUSE IS NOT AVAILABLE", destination);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(response);
    }
}
