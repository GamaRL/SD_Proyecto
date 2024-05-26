package mx.unam.fi.distributed.messages.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.models.Device;
import mx.unam.fi.distributed.messages.repositories.BranchRepository;
import mx.unam.fi.distributed.messages.repositories.DeviceRepository;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final Semaphore lock;
    private final DeviceRepository deviceRepository;
    private final NodeRepository nodeRepository;
    private final Client client;
    private final BranchRepository branchRepository;

    @Value("${app.server.node_n}")
    private int node_n;

    public void create(String name, String type, String serialNumber) {

        try {
            lock.acquire();

            var masterId = nodeRepository.getNodesId().stream().mapToInt(Integer::intValue).max().orElse(-1);
            var masterNode = nodeRepository.getNode(masterId);
            var message = String.format("CREATE-DEVICE-TO-MASTER;%s;%s;%s", name, type, serialNumber);

            client.sendMessage(masterNode, new Message(node_n, message, LocalDateTime.now()));

            lock.release();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public void createFromMaster(String name, String type, String serialNumber) {

        try {
            lock.acquire();

            var branch = branchRepository.findAllById(nodeRepository.getNodesId().stream().mapToLong(i -> i).boxed().toList())
                    .stream()
                    .min(Comparator.comparing(b -> b.getDevices().size()))
                    .orElseThrow();

            Thread.sleep(3000);

            var device = deviceRepository.save(new Device(null, name, type, serialNumber, branch));

            var message = String.format("CREATE-DEVICE-FROM-MASTER;%s;%s;%s;%s;%s",
                    device.getId(),
                    device.getName(),
                    device.getType(),
                    device.getSerialNumber(),
                    device.getBranch().getId());

            nodeRepository.getOtherNodes().forEach(n -> client.sendMessage(n, new Message(node_n, message, LocalDateTime.now())));

            lock.release();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public void forceCreate(Long id, String name, String type, String serialNumber, Long branchId) {
        var branch = branchRepository.findById(branchId).orElseThrow();
        deviceRepository.save(new Device(id, name, type, serialNumber, branch));
    }
}
