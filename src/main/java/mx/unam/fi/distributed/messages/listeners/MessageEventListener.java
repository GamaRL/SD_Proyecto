package mx.unam.fi.distributed.messages.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import mx.unam.fi.distributed.messages.services.AppUserService;
import mx.unam.fi.distributed.messages.storage.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener implements ApplicationListener<MessageEvent>{

    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);

    private final MessageRepository messageRepository;
    private final Semaphore lock;
    private final Client client;
    private final NodeRepository nodeRepository;
    private final AppUserService appUserService;

    @Value("${app.server.node_n}")
    private int nodeN;

    private void processMessage(Message message) throws InterruptedException {

        Runnable process = () -> {
            var args = message.message().split(";");
            var type = args[0];

            log.info("Processing message type: {}", type);
            switch (type) {
                case "TOKEN":
                    var masterId = nodeRepository.getNodesId().stream().mapToInt(i -> i).max().orElse(nodeN);
                    if (masterId == Integer.parseInt(args[1])) {
                        System.out.println(lock.availablePermits());

                        lock.release();

                        Thread.yield();

                        try {
                            lock.acquire();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        var newToken = String.format("TOKEN;%d", masterId);

                        client.sendMessage(
                                nodeRepository.getNextNode(nodeN),
                                new Message(
                                        nodeN,
                                        newToken,
                                        LocalDateTime.now()
                                )
                        );
                    }
                    break;

                case "HELLO":

                    if (!nodeRepository.containsNode(message.from()))
                        nodeRepository.addNode(message.from());

                    break;

                case "CREATE-APP-USER":

                    System.out.println(args);
                    appUserService.forceCreate(Long.parseLong(args[1]), args[2], args[3], args[4]);

                    break;

                default:
                    break;
            }
        };

        executor.execute(process);
    }

    @Override
    public void onApplicationEvent(MessageEvent event) {

        Message message = null;
        Message response = null;

        try (
                var socket = event.getSocket();
                var out = new ObjectOutputStream(socket.getOutputStream());
                var in = new ObjectInputStream(socket.getInputStream())
        ) {
            message = (Message) in.readObject();
            response = new Message(nodeN, "ACCEPTED", LocalDateTime.now());
            out.writeObject(response);

        } catch (IOException | ClassNotFoundException e) {
            log.info("An unexpected error occurred '{}'", e.getMessage());
        }

        try {
            if (message != null) {
                processMessage(message);
                messageRepository.saveMessage(message);
                messageRepository.saveMessage(response);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
