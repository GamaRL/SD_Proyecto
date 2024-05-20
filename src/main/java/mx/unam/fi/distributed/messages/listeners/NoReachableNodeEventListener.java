package mx.unam.fi.distributed.messages.listeners;

import lombok.AllArgsConstructor;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NoReachableNodeEventListener implements ApplicationListener<NoReachableNodeEvent> {

    private final NodeRepository nodeRepository;

    @Override
    @EventListener
    public void onApplicationEvent(NoReachableNodeEvent event) {
        nodeRepository.removeNode(event.getNode().id());
    }
}
