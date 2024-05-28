package mx.unam.fi.distributed.messages.settings;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.server.IMessageServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.util.concurrent.*;

@Configuration
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class AppConfiguration {

    private final IMessageServer server;
    private final HeartBeat heartBeat;


    /**
     * Define una cola bloqueante para almacenar mensajes entrantes.
     * @return una instancia de LinkedBlockingQueue para mensajes.
     */
    @Bean
    public BlockingQueue<Message> incomingMessages() {
        return new LinkedBlockingQueue<>();
    }

    /**
     * Define un semáforo global para gestionar la exclusión mutua.
     * @return una instancia de Semaphore con 0 permisos iniciales.
     */
    @Bean
    public Semaphore globalLock() {

        return new Semaphore(0);
    }

    /**
     * Inicializa los hilos del servidor y del heartbeat después de la construcción del bean.
     * @throws IOException si ocurre un error al iniciar el servidor.
     * @throws InterruptedException si ocurre una interrupción durante el sleep del hilo.
     */
    @PostConstruct
    public void initThreads() throws IOException, InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        server.listen();

        // Espera un segundo antes de ejecutar los hilos
        Thread.sleep(1000);
        executor.execute(server);
        executor.execute(heartBeat);
    }
}
