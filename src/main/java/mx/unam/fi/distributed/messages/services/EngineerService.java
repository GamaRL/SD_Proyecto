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
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class EngineerService {

    private final EngineerRepository engineerRepository;
    private final Semaphore lock;
    private final NodeRepository nodeRepository;
    private final Client client;

    @Value("${app.server.node_n}")
    private int node_n;

    public List<Engineer> getAll() {
        return engineerRepository.findAll();
    }

    public void create(String name, String speciality) {

        try {
            lock.acquire();

            // Creación de un nuevo ingeniero
            Engineer engineer = new Engineer(null, name, speciality);
            engineerRepository.save(engineer);

            var message = String.format("CREATE-ENGINEER;%s;%s;%s", engineer.getId(), engineer.getName(), engineer.getSpeciality());

            // Notificar a los demás nodos
            nodeRepository.getOtherNodes().forEach(n -> client.sendMessage(n, new Message(node_n, message, LocalDateTime.now())));

            lock.release();
        } catch (InterruptedException e) {
            log.error("An error happened when creating engineer: '{}'", e.getMessage());
        }
    }

    public void forceCreate(Long id, String name, String speciality) {
        Engineer engineer = new Engineer(id, name, speciality);
        engineerRepository.save(engineer);
    }

}
