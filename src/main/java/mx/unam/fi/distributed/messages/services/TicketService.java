package mx.unam.fi.distributed.messages.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.models.Engineer;
import mx.unam.fi.distributed.messages.models.Ticket;
import mx.unam.fi.distributed.messages.repositories.EngineerRepository;
import mx.unam.fi.distributed.messages.repositories.NodeRepository;
import mx.unam.fi.distributed.messages.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final AppUserService appUserService;
    private final EngineerRepository engineerRepository;
    private final NodeRepository nodeRepository;
    private final DeviceService deviceService;
    private final Semaphore globalLock;
    private final Client client;
    private final EngineerService engineerService;
    private final TicketRepository ticketRepository;

    @Value("${app.server.node_n}")
    private int node_n;

    private Engineer getNextAvailableEngineer() {
        return engineerRepository.findAll()
                .stream()
                .min(Comparator.comparing(e -> e.getTickets().size()))
                .orElseThrow();
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public void create(String description, Long userId, Long deviceId) {

        try {
            globalLock.acquire();

            var invoice = UUID.randomUUID().toString();
            var openDate = LocalDateTime.now();
            var user = appUserService.findById(userId);
            var engineer = getNextAvailableEngineer();
            var device = deviceService.findById(deviceId);
            description = description.replace(";", "");

            var ticket = ticketRepository.save(new Ticket(null, invoice, openDate, null, description, user, device, engineer));

            var message = String.format("CREATE-TICKET;%s;%s;%s;%s;%s;%s;%s",
                    ticket.getId(),
                    ticket.getInvoice(),
                    ticket.getOpenDate(),
                    ticket.getDescription(),
                    ticket.getUser().getId(),
                    ticket.getDevice().getId(),
                    ticket.getEngineer().getId());

            nodeRepository
                    .getOtherNodes()
                    .forEach(n -> client.sendMessage(n, new Message(node_n, message, LocalDateTime.now())));

            globalLock.release();
        } catch (InterruptedException e) {
            log.error("");
        }
    }

    public void forceCreate(Long id, String invoice, LocalDateTime openDate, String description, Long userId, Long engineerId, Long deviceId) {

        var user = appUserService.findById(userId);
        var engineer = engineerService.findById(engineerId);
        var device = deviceService.findById(deviceId);

        var ticket = new Ticket(id, invoice, openDate, null, description, user, device, engineer);
        ticketRepository.save(ticket);
    }

    public void close(Long ticketId) {

        try {
            globalLock.acquire();

            var ticket = ticketRepository.findById(ticketId).orElseThrow();

            ticket.setCloseDate(LocalDateTime.now());

            ticketRepository.save(ticket);

            var message = String.format("CLOSE-TICKET;%s;%s",
                    ticket.getId(),
                    ticket.getCloseDate());

            nodeRepository
                    .getOtherNodes()
                    .forEach(n -> client.sendMessage(n, new Message(node_n, message, LocalDateTime.now())));

            globalLock.release();
        } catch (InterruptedException e) {
            log.error("");
        }
    }

    public void forceClose(Long ticketId, LocalDateTime closeDate) {

        var ticket = ticketRepository.findById(ticketId).orElseThrow();
        ticket.setCloseDate(closeDate);
        ticketRepository.save(ticket);
    }
}
