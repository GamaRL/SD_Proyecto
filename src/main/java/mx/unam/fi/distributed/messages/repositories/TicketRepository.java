package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz de un Ticket en el sistema distribuido.
 * Proporciona métodos para interactuar con los tickets en la base de datos
 * de forma automática.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> { }
