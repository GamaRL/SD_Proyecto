package mx.unam.fi.distributed.messages.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.models.AppUser;
import mx.unam.fi.distributed.messages.models.Engineer;
import mx.unam.fi.distributed.messages.repositories.EngineerRepository;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class EngineerService {

    // Inyección de dependencias necesarias para el servicio
    private final EngineerRepository engineerRepository;
    private final Semaphore lock;
    private final NodeRepository nodeRepository;
    private final Client client;

    // Identificador del nodo actual, configurado a través de properties
    @Value("${app.server.node_n}")
    private int node_n;

    /**
     * Obtiene una lista de todos los ingenieros.
     *
     * @return Lista de todos los ingenieros.
     */
    public List<Engineer> getAll() {
        return engineerRepository.findAll();
    }

    /**
     * Busca un ingeniero por su ID.
     *
     * @param id El ID del ingeniero.
     * @return El ingeniero encontrado, o null si no se encuentra.
     */
    public Engineer findById(Long id) {
        return engineerRepository
                .findById(id)
                .orElse(null);
    }

    /**
     * Crea un nuevo ingeniero y lo guarda en el repositorio.
     * Utiliza exclusión mutua para evitar conflictos en la creación.
     *
     * @param name El nombre del ingeniero.
     * @param speciality La especialidad del ingeniero.
     */
    public void create(String name, String speciality) {

        try {
            lock.acquire();

            // Creación de un nuevo ingeniero
            Engineer engineer = new Engineer(null, name, speciality, new ArrayList<>());
            engineer = engineerRepository.save(engineer);

            var message = String.format("CREATE-ENGINEER;%s;%s;%s", engineer.getId(), engineer.getName(), engineer.getSpeciality());

            // Notificar a los demás nodos
            nodeRepository.getOtherNodes().forEach(n -> client.sendMessage(n, new Message(node_n, message, LocalDateTime.now())));

            lock.release();
        } catch (InterruptedException e) {
            log.error("An error happened when creating engineer: '{}'", e.getMessage());
        }
    }

    /**
     * Fuerza la creación de un ingeniero en un nodo, sincronizando los datos entre nodos.
     *
     * @param id El ID del ingeniero.
     * @param name El nombre del ingeniero.
     * @param speciality La especialidad del ingeniero.
     */
    public void forceCreate(Long id, String name, String speciality) {
        Engineer engineer = new Engineer(id, name, speciality, new ArrayList<>());
        engineerRepository.save(engineer);
    }

}
