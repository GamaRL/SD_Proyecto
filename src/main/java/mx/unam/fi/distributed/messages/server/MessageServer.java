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
 * Servicio que implementa un servidor de mensajes del sistema distribuido.
 * Este servidor escucha en un puerto específico, acepta conexiones y publica eventos de mensajes.
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


    /**
     * Inicia el servidor de mensajes, escuchando en el puerto configurado.
     * @throws IOException si ocurre un error al abrir el socket del servidor.
     */
    @Override
    public void listen() throws IOException {
        socket = new ServerSocket(port);
        isAlive = true;
        log.info("Message server is listening on port {}", port);
    }

    /**
     * Detiene el servidor de mensajes y cierra el socket.
     * @throws IOException si ocurre un error al cerrar el socket del servidor.
     */
    @Override
    public void kill() throws IOException {
        isAlive = false;
        socket.close();
        log.info("Message server is down");
    }

    /**
     * Verifica si el servidor de mensajes está activo.
     * @return true si el servidor está activo, false en caso contrario.
     */
    @Override
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Método que se ejecuta en un hilo separado para aceptar conexiones entrantes.
     * Publica un evento de mensaje por cada conexión aceptada.
     */
    @Override
    public void run() {

        try {
            while (isAlive()) {
                eventPublisher.publishEvent(new MessageEvent(this, socket.accept()));
            }
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }
}
