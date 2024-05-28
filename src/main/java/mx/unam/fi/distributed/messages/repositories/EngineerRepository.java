package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.models.Engineer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz de repositorio para la entidad Engineer.
 */
@Repository
public interface EngineerRepository extends JpaRepository<Engineer, Long> {}
