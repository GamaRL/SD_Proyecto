package mx.unam.fi.distributed.messages.listeners;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.net.Socket;

/**
 * Representa un evento de los mensajes que se reciben a través de una conexión de socket
 */
@Getter
public class MessageEvent extends ApplicationEvent {

    private final Socket socket;

    /**
     * Constructor de un nuevo MessageEvent
     * 
     * @param source Objeto en el que ocurre inicialmente el evento
     * @param socket Socket a traves del cual se recibió el mensaje
     */
    public MessageEvent(Object source, Socket socket) {
        super(source);
        this.socket = socket;
    }
}
