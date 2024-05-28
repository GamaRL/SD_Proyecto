package mx.unam.fi.distributed.messages.settings;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mx.unam.fi.distributed.messages.client.IClient;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import mx.unam.fi.distributed.messages.services.DeviceService;
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
    private int nodeN;

    /**
     * Envía un mensaje multicast a todos los nodos conocidos.
     */
    private void sendMulticastMessage() {
        nodeRepository
                .getNodes()
                .forEach(n -> client.sendMessage(n, new Message(nodeN, "HELLO", LocalDateTime.now())));
    }

    @Override
    public void run() {

        boolean isAlive = true;
        boolean isMaster = false;

        while (isAlive) {
            try {
                // Pausa la ejecución durante 2 segundos
                Thread.sleep(2000);

                // Envía un mensaje multicast a todos los nodos
                sendMulticastMessage();

                // Determina el nodo maestro basándose en el ID más alto
                int masterId = nodeRepository.getNodesId().stream().mapToInt(i -> i).max().orElse(-1);

                if (masterId == nodeN) {
                    System.out.println("> Yo soy el master");

                    var currentNodeId = TokenInfo.getCurrentNodeId();

                    log.info("> Current node wih token: {}", currentNodeId);
                    log.info("{}", nodeRepository.getNodesId());

                    if (!isMaster || !nodeRepository.containsNode(currentNodeId)) {
                        var newToken = String.format("TOKEN;%d", nodeN);
                        client.sendMessage(nodeRepository.getNode(nodeN), new Message(nodeN, newToken, LocalDateTime.now()));
                    }
                    log.info("{}", nodeRepository.getNodesId());

                    isMaster = true;
                } else {
                    isMaster = false;
                }
            } catch (InterruptedException e) {
                isAlive = false;
                log.error("Error {}", e.getMessage());
            }
        }
    }
}
