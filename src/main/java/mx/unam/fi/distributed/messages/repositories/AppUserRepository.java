package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz de repositorio para la entidad AppUser (usuarios).
 * Proporciona métodos para interactuar con los usuarios de la aplicación en la base de datos
 * de forma automática.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {}
