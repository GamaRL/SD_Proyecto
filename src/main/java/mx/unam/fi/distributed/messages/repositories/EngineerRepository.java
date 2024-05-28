package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.models.Engineer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz de repositorio para la entidad Engineer (ingeniero).
 * Proporciona métodos para interactuar con los ingenieros en la base de datos
 * de forma automática.
 */
@Repository
public interface EngineerRepository extends JpaRepository<Engineer, Long> {}
