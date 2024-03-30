package mx.unam.fi.distributed.messages.settings;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.node.Node;
import mx.unam.fi.distributed.messages.server.IServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AppConfiguration {

    private final IServer server;

    @Value("${app.server.host}")
    private String host;

    private final List<Node> nodeList = List.of(
            new Node("Nodo 1", "node_1", 5000),
            new Node("Nodo 2", "node_2", 5000)
    );

    @PostConstruct
    public void initThreads() {
        server.start();

        try {
            Thread.sleep(1000);

            for (var node : nodeList) {
                new Client().sendMessage(node, String.format("Message from %s to %s", host, node.host()));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
