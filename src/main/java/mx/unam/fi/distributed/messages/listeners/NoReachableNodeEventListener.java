package mx.unam.fi.distributed.messages.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import mx.unam.fi.distributed.messages.services.DeviceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoReachableNodeEventListener implements ApplicationListener<NoReachableNodeEvent> {

    private final NodeRepository nodeRepository;
    private final DeviceService deviceService;
    private final Client client;

    @Value("${app.server.node_n}")
    private int nodeN;

    @Override
    public void onApplicationEvent(NoReachableNodeEvent event) {
        nodeRepository.removeNode(event.getNode().id());

        var masterId = nodeRepository
                .getNodesId()
                .stream()
                .mapToInt(i -> i)
                .max().orElse(nodeN);

        if (nodeN == masterId) {
            new Thread(() -> deviceService.adjustNodesFromMaster((long) event.getNode().id())).start();
        }
    }
}
