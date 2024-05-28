package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.models.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz de repositorio para la entidad Device (dispositivo).
 * Proporciona métodos para interactuar con los dispositivos en la base de datos
 * de forma automática.
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {}
