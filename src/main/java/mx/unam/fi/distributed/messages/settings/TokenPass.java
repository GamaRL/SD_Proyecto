package mx.unam.fi.distributed.messages.settings;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import mx.unam.fi.distributed.messages.services.AppUserService;
import mx.unam.fi.distributed.messages.services.DeviceService;
import mx.unam.fi.distributed.messages.services.EngineerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenPass implements Runnable {

    private final AppUserService appUserService;
    private final DeviceService deviceService;
    private final EngineerService engineerService;

    @Value("${app.server.node_n}")
    private int node_n;

    @PostConstruct
    void init() {
        new Thread(this).start();
    }

    @Override
    public void run() {

        int i = 0;

        while (true) {

            //appUserService.create(String.format("Person %d %d", node_n, i++), UUID.randomUUID().toString(), "555555");
            //deviceService.create("Nueva compu", "Computer", UUID.randomUUID().toString().substring(0, 5));
            //engineerService.create("El Inge " + i, "Computers");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
