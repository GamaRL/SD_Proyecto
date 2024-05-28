package mx.unam.fi.distributed.messages.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.listeners.MessageEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Servidor encargado de escuchar conexiones entrantes y publicar eventos de mensajes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServer implements IMessageServer {

    private ServerSocket socket;
    private boolean isAlive = false;

    @Value("${app.server.port}")
    private int port;

    private final ApplicationEventPublisher eventPublisher;


    @Override
    public void listen() throws IOException {
        socket = new ServerSocket(port);
        isAlive = true;
        log.info("Message server is listening on port {}", port);
    }

    @Override
    public void kill() throws IOException {
        isAlive = false;
        socket.close();
        log.info("Message server is down");
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void run() {

        try {
            // Mientras el servidor este vivo, se ejecuta.
            while (isAlive()) {
                // Se acepta una nueva conexión entrante y se publica un evento de mensaje
                eventPublisher.publishEvent(new MessageEvent(this, socket.accept()));
            }
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }
}
