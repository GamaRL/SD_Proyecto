package mx.unam.fi.distributed.messages.listeners;

import lombok.Getter;
import mx.unam.fi.distributed.messages.node.Node;
import org.springframework.context.ApplicationEvent;


/**
 * Es usada cuando se produce una excepcion de nodo inalcanzable.
 * Un nodo se vuelve inalcanzable cuando se pasa el tiempo de espera o algun
 * error en el socket.
 */
@Getter
public class NoReachableNodeEvent extends ApplicationEvent {
    private final Node node;

    public NoReachableNodeEvent(Object source, Node node) {
        super(source);
        this.node = node;
    }
}
