package mx.unam.fi.distributed.messages.settings;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import mx.unam.fi.distributed.messages.client.Client;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
@AllArgsConstructor
public class TokenPass implements Runnable {

    private final Semaphore lock;

    @PostConstruct
    void init()
    {
        new Thread(this).start();
    }

    @Override
    public void run() {

        while (true) {
            try {
                lock.acquire();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("I have the token");

            lock.release();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
