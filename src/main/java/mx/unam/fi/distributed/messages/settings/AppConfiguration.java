package mx.unam.fi.distributed.messages.settings;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.node.Node;
import mx.unam.fi.distributed.messages.server.IMessageServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfiguration {

    private final IMessageServer server;

    @Bean
    public Map<String, Node> host() {
        return Map.of(
                "node_1", new Node("node_1", "172.16.114.128", 5000),
                "node_2", new Node("node_2", "172.16.114.129", 5000),
                "node_3", new Node("node_3", "172.16.114.130", 5000),
                "node_4", new Node("node_4", "172.16.114.131", 5000)
        );
    }

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
