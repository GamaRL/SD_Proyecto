package mx.unam.fi.distributed.messages.repositories;

import mx.unam.fi.distributed.messages.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {}
