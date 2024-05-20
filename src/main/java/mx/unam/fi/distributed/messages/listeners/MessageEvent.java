package mx.unam.fi.distributed.messages.listeners;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.net.Socket;

@Getter
public class MessageEvent extends ApplicationEvent {

    private final Socket socket;

    public MessageEvent(Object source, Socket socket) {
        super(source);
        this.socket = socket;
    }
}
