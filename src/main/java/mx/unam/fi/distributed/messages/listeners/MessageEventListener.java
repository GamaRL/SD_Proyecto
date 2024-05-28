package mx.unam.fi.distributed.messages.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import mx.unam.fi.distributed.messages.services.AppUserService;
import mx.unam.fi.distributed.messages.services.DeviceService;
import mx.unam.fi.distributed.messages.services.EngineerService;
import mx.unam.fi.distributed.messages.services.TicketService;
import mx.unam.fi.distributed.messages.settings.TokenInfo;
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

/**
 * Espera los mensajes entrantes de MessageEvent
 */
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
    private final DeviceService deviceService;
    private final EngineerService engineerService;
    private final TicketService ticketService;

    @Value("${app.server.node_n}")
    private int nodeN;

    private void processMessage(Message message) throws InterruptedException {

        Runnable process = () -> {
            var args = message.message().split(";");
            var type = args[0];

            log.info("Processing message type: {}", type);

            var masterId = nodeRepository.getNodesId().stream().mapToInt(i -> i).max().orElse(nodeN);

            switch (type) {
                case "TOKEN":
                    if (masterId == Integer.parseInt(args[1])) {
                        System.out.println(lock.availablePermits());

                        lock.release();

                        Thread.yield();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            lock.acquire();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        // Notifica a quién le manda el token
                        client.sendMessage(
                            nodeRepository.getNode(masterId),
                            new Message(
                                nodeN,
                                String.format("HAS-TOKEN;%s", nodeRepository.getNextNode(nodeN).id()),
                                LocalDateTime.now()
                            )
                        );

                        // Manda el token al siguiente nodo
                        client.sendMessage(
                            nodeRepository.getNextNode(nodeN),
                            new Message(
                                nodeN,
                                message.message(),
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

                    appUserService.forceCreate(Long.parseLong(args[1]), args[2], args[3], args[4]);

                    break;

                case "CREATE-ENGINEER":

                    engineerService.forceCreate(Long.parseLong(args[1]), args[2], args[3]);

                    break;

                case "CREATE-DEVICE-TO-MASTER":
                    if (masterId == nodeN) {
                        deviceService.createFromMaster(args[1], args[2], args[3]);
                    }
                    break;

                case "CREATE-DEVICE-FROM-MASTER":
                    deviceService.forceCreate(Long.parseLong(args[1]), args[2], args[3], args[4], Long.parseLong(args[5]));
                    break;

                case "UPATE-DEVICE-BRANCH":
                    deviceService.updateDeviceBranch(Long.parseLong(args[1]), Long.parseLong(args[2]));
                    break;

                case "HAS-TOKEN":

                    TokenInfo.setCurrentNode(Integer.parseInt(args[1]));
                    log.info("NODE {} has the token", TokenInfo.getCurrentNodeId());
                    break;

                case "CREATE-TICKET":
                    ticketService.forceCreate(
                            Long.parseLong(args[1]),
                            args[2],
                            LocalDateTime.parse(args[3]),
                            args[4],
                            Long.parseLong(args[5]),
                            Long.parseLong(args[6]),
                            Long.parseLong(args[7])
                    );
                    break;

                case "CLOSE-TICKET":
                    ticketService.forceClose(Long.parseLong(args[1]), LocalDateTime.parse(args[2]));
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
