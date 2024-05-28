package mx.unam.fi.distributed.messages.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.models.AppUser;
import mx.unam.fi.distributed.messages.repositories.AppUserRepository;
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
public class AppUserService {

    private final Semaphore lock;
    private final AppUserRepository appUserRepository;
    private final Client client;
    private final NodeRepository nodeRepository;

    @Value("${app.server.node_n}")
    private int node_n;

    public List<AppUser> getAll() {
        return appUserRepository.findAll();
    }

    public AppUser findById(Long id) {
        return appUserRepository.findById(id).orElse(null);
    }

    /**
     * Método para realizar la inserción de un usuario desde el mismo nodo de ejecución. A través
     * de este método, el usuario se crea, se le asigna un id y se dispara la inserción del usuario
     * en los demás nodos.
     * @param name el nombre del usuario
     * @param mail el correo electrónico del usuario
     * @param telephone el número e teléfono del usuario
     */
    public void create(String name, String mail, String telephone) {

        try {
            lock.acquire();

            var user = appUserRepository.save(new AppUser(null, name, mail, telephone, new ArrayList<>()));
            var message = String.format("CREATE-APP-USER;%s;%s;%s;%s", user.getId(), user.getName(), user.getMail(), user.getTelephone());

            // Notificar a los demás nodos
            nodeRepository.getOtherNodes().forEach(n -> client.sendMessage(n, new Message(node_n, message, LocalDateTime.now())));

            lock.release();
        } catch (InterruptedException e) {
            log.error("An error happened when creating user: '{}'", e.getMessage());
        }
    }

    /**
     * Método para realizar la inserción del usuario que se realizó desde otro nodo
     * @param id el id asignado al usuario en el nodo de creación
     * @param name el nombre del usuario
     * @param mail el correo electrónico del usuario
     * @param telephone el número e teléfono del usuario
     */
    public void forceCreate(Long id, String name, String mail, String telephone) {
        var user = appUserRepository.save(new AppUser(id, name, mail, telephone, new ArrayList<>()));

        log.info("User was created: {}", user);
    }
}
