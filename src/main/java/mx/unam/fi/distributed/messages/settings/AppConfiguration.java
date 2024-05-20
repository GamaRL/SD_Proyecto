package mx.unam.fi.distributed.messages.settings;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.client.IClient;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.node.Node;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import mx.unam.fi.distributed.messages.server.IMessageServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class AppConfiguration {

    private final IMessageServer server;
    private final IClient client;
    private final NodeRepository nodeRepository;

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

        Thread.sleep(500);

        nodeRepository.getNodes().forEach((n) -> {
            System.out.println(n);
            var res = client.sendMessage(n, new Message("Me.", "Hola", LocalDateTime.now()));
            System.out.println(res);
        });
    }
}
