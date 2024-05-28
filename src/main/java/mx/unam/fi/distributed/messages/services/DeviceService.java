package mx.unam.fi.distributed.messages.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.models.Branch;
import mx.unam.fi.distributed.messages.models.Device;
import mx.unam.fi.distributed.messages.repositories.BranchRepository;
import mx.unam.fi.distributed.messages.repositories.DeviceRepository;
import mx.unam.fi.distributed.messages.repositories.EngineerRepository;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    // Inyección de dependencias necesarias para el servicio
    private final Semaphore lock;
    private final DeviceRepository deviceRepository;
    private final NodeRepository nodeRepository;
    private final Client client;
    private final BranchRepository branchRepository;

    // Identificador del nodo actual, configurado a través de properties
    @Value("${app.server.node_n}")
    private int node_n;

    /**
     * Método que se ejecuta después de la construcción del bean.
     * Inicializa las sucursales en el repositorio.
     */
    @PostConstruct
    public void init() {

        branchRepository.save(new Branch(1L, "Sucursal 1", "Call1 1, Colonia 1", new ArrayList<>()));
        branchRepository.save(new Branch(2L, "Sucursal 2", "Call1 2, Colonia 2", new ArrayList<>()));
        branchRepository.save(new Branch(3L, "Sucursal 3", "Call1 3, Colonia 3", new ArrayList<>()));
    }

    /**
     * Busca un dispositivo por su ID.
     *
     * @param id El ID del dispositivo.
     * @return El dispositivo encontrado, o null si no se encuentra.
     */
    public Device findById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    /**
     * Encuentra todos los dispositivos disponibles.
     *
     * @return Lista de dispositivos disponibles.
     */
    public List<Device> findAllAvailable() {
        return deviceRepository.findAll()
                .stream()
                .filter(d -> d.getTickets().stream().noneMatch(t -> t.getCloseDate() == null))
                .collect(Collectors.toList());
    }

    /**
     * Encuentra todos los dispositivos de la sucursal actual.
     *
     * @return Lista de dispositivos de la sucursal actual.
     */
    public List<Device> findAllOfCurrentBranch() {
        return deviceRepository.findAll()
                .stream()
                .filter(d -> d.getBranch().getId() == node_n)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo dispositivo y envía un mensaje al nodo maestro para su creación.
     * Utiliza exclusión mutua para evitar conflictos en la creación.
     *
     * @param name El nombre del dispositivo.
     * @param type El tipo de dispositivo.
     * @param serialNumber El número de serie del dispositivo.
     */
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

    /**
     * Obtiene la siguiente sucursal disponible con menos dispositivos.
     *
     * @return La sucursal disponible.
     */
    private Branch getNextAvailableBranch() {
        return branchRepository.findAllById(nodeRepository.getNodesId().stream().mapToLong(i -> i).boxed().toList())
                .stream()
                .min(Comparator.comparing(b -> b.getDevices().size()))
                .orElseThrow();
    }

    /**
     * Crea un nuevo dispositivo desde el nodo maestro y distribuye el dispositivo entre las sucursales.
     *
     * @param name El nombre del dispositivo.
     * @param type El tipo de dispositivo.
     * @param serialNumber El número de serie del dispositivo.
     */
    public void createFromMaster(String name, String type, String serialNumber) {

        try {
            lock.acquire();

            var branch = getNextAvailableBranch();

            var device = deviceRepository.save(new Device(null, name, type, serialNumber, branch, new ArrayList<>()));

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

    /**
     * Fuerza la creación de un dispositivo en una sucursal específica.
     *
     * @param id El ID del dispositivo.
     * @param name El nombre del dispositivo.
     * @param type El tipo de dispositivo.
     * @param serialNumber El número de serie del dispositivo.
     * @param branchId El ID de la sucursal donde se creará el dispositivo.
     */
    public void forceCreate(Long id, String name, String type, String serialNumber, Long branchId) {
        var branch = branchRepository.findById(branchId).orElseThrow();
        deviceRepository.save(new Device(id, name, type, serialNumber, branch, new ArrayList<>()));
    }

    /**
     * Ajusta la distribución de dispositivos entre las sucursales desde el nodo maestro.
     *
     * @param nodeId El ID del nodo que necesita ajuste.
     */
    public void adjustNodesFromMaster(Long nodeId) {

        try {
            lock.acquire();

            branchRepository.findById(nodeId)
                .orElseThrow()
                .getDevices()
                .forEach(d -> {
                    var branch = getNextAvailableBranch();
                    d.setBranch(getNextAvailableBranch());
                    deviceRepository.save(d);

                    var message = String.format(
                        "UPATE-DEVICE-BRANCH;%s;%s",
                        d.getId(),
                        branch.getId()
                    );
                    nodeRepository.getOtherNodes().forEach(n -> client.sendMessage(n, new Message(node_n, message, LocalDateTime.now())));
                });

            lock.release();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Actualiza la sucursal de un dispositivo específico.
     *
     * @param deviceId El ID del dispositivo.
     * @param branchId El ID de la nueva sucursal.
     */
    public void updateDeviceBranch(Long deviceId, Long branchId) {
        var branch = branchRepository.findById(branchId).orElseThrow();
        var device = deviceRepository.findById(deviceId).orElseThrow();

        device.setBranch(branch);

        deviceRepository.save(device);
    }
}
```