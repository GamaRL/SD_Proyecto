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

    /**
     * Método de inicialización que se ejecuta después de la construcción del objeto.
     * Inicia un nuevo hilo que ejecuta el método run() de esta clase.
     */
    @PostConstruct
    void init() 
    {
        new Thread(this).start();
    }

    /**
     * Método principal que se ejecuta en el hilo.
     * Realiza un ciclo infinito donde intenta adquirir el semáforo, imprime un mensaje,
     * y luego libera el semáforo.
     */
    @Override
    public void run() {
        
        while (true) {
            try {
                // Adquiere el semáforo para asegurar la exclusión mutua
                lock.acquire();
            } catch (InterruptedException e) {
                // Maneja la excepción de interrupción del hilo
                System.out.println(e.getMessage());
            }

            // Imprime un mensaje indicando que tiene el token
            System.out.println("I have the token");

            // Libera el semáforo
            lock.release();

            try {
                // Pausa la ejecución del hilo por 500 milisegundos
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Maneja la excepción de interrupción del hilo y lanza una RuntimeException
                throw new RuntimeException(e);
            }
        }
    }
}
