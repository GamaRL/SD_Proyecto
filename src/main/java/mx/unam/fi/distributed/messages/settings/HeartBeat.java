package mx.unam.fi.distributed.messages.settings;

import lombok.RequiredArgsConstructor;
import mx.unam.fi.distributed.messages.client.IClient;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class HeartBeat implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HeartBeat.class);
    private final IClient client;
    private final NodeRepository nodeRepository;

    @Value("${app.server.node_n}")
    private int node_n;

    private void sendMulticastMessage() {
        nodeRepository.getNodes().forEach((n) -> {
            System.out.println(n);
            client.sendMessage(n, new Message("Me.", "Hola", LocalDateTime.now()));
        });
    }

    @Override
    public void run() {

        boolean isAlive = true;
        while (isAlive) {
            try {
                sendMulticastMessage();
                Thread.sleep(5000);

                int masterId = nodeRepository.getNodesId().stream().mapToInt(i -> i).max().orElse(node_n);

                if (masterId == node_n) {
                    System.out.println("> Yo soy el master");
                }
            } catch (InterruptedException e) {
                isAlive = false;
                log.error(e.getMessage());
            }
        }
    }
}
