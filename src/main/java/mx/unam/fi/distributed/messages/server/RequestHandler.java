package mx.unam.fi.distributed.messages.server;


import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.storage.MessageRepository;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class RequestHandler implements Runnable {
    private final BlockingQueue<Socket> pendingRequests;
    private final BlockingQueue<Message> incomingMessages;
    private final MessageRepository messageRepository;
    private final String hostName;

    public RequestHandler(String hostName, BlockingQueue<Socket> pendingRequests, BlockingQueue<Message> incomingMessages, MessageRepository messageRepository) {
        this.pendingRequests = pendingRequests;
        this.incomingMessages = incomingMessages;
        this.messageRepository = messageRepository;
        this.hostName = hostName;
    }

    private Message processRequest() {

        Message message = null;
        try (
                var socket = pendingRequests.take();
                var out = new ObjectOutputStream(socket.getOutputStream());
                var in = new ObjectInputStream(socket.getInputStream())
        ) {
            message = (Message) in.readObject();
            var response = new Message(hostName, "ACCEPTED", LocalDateTime.now());
            out.writeObject(response);
            incomingMessages.add(message);
            messageRepository.saveMessage(message);
            messageRepository.saveMessage(response);

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            log.info("An unexpected error occurred '{}'", e.getMessage());
        }

        return message;
    }

    @Override
    public void run() {
        do {
            Message response = processRequest();
            log.info("New message '{}' at '{}'", response.message(), response.timestamp());
        } while (true);
    }
}
