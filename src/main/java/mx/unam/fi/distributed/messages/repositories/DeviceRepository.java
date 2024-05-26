package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.models.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {}
