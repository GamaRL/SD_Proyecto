package mx.unam.fi.distributed.messages.settings;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.server.IMessageServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class AppConfiguration {

    private final IMessageServer server;
    private final HeartBeat heartBeat;


    @Bean
    public BlockingQueue<Message> incomingMessages() {
        return new LinkedBlockingQueue<>();
    };

    @PostConstruct
    public void initThreads() throws IOException, InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        server.listen();

        Thread.sleep(1000);
        executor.execute(server);
        executor.execute(heartBeat);
    }
}
