package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz de un ticket en el sistema distribuido.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> { }
