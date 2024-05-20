package mx.unam.fi.distributed.messages.listeners;

import lombok.Getter;
import mx.unam.fi.distributed.messages.node.Node;
import org.springframework.context.ApplicationEvent;

@Getter
public class NoReachableNodeEvent extends ApplicationEvent {
    private final Node node;

    public NoReachableNodeEvent(Object source, Node node) {
        super(source);
        this.node = node;
    }
}
