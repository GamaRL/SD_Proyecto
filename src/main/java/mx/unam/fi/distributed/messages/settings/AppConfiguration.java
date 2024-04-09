package mx.unam.fi.distributed.messages.settings;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.node.Node;
import mx.unam.fi.distributed.messages.server.IMessageServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfiguration {

    private final IMessageServer server;

    @Value("${app.server.host}")
    private String host;

    private final List<Node> nodeList = List.of(
            new Node("Nodo 1", "node_1", 5000),
            new Node("Nodo 2", "node_2", 5000)
    );

    @Bean
    public BlockingQueue<Message> incomingMessages() {
        return new LinkedBlockingQueue<>();
    };

    @PostConstruct
    public void initThreads() throws InterruptedException {
        try {
            server.listen();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        new Thread(server).start();
    }
}
