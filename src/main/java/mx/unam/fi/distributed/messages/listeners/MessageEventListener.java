package mx.unam.fi.distributed.messages.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.storage.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener implements ApplicationListener<MessageEvent>{

    private final MessageRepository messageRepository;

    @Value("${HOST}")
    private String hostName;

    @Override
    @EventListener
    public void onApplicationEvent(MessageEvent event) {

        try (
                var socket = event.getSocket();
                var out = new ObjectOutputStream(socket.getOutputStream());
                var in = new ObjectInputStream(socket.getInputStream())
        ) {
            var message = (Message) in.readObject();
            var response = new Message(hostName, "ACCEPTED", LocalDateTime.now());
            out.writeObject(response);
            messageRepository.saveMessage(message);
            messageRepository.saveMessage(response);

        } catch (IOException | ClassNotFoundException e) {
            log.info("An unexpected error occurred '{}'", e.getMessage());
        }
    }
}
