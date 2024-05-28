package mx.unam.fi.distributed.messages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Clase principal para la aplicación del sistema distribuido de tickets de soporte
 * Anotada con @SpringBootApplication para indicar que es una aplicación Spring Boot.
 */
@SpringBootApplication
public class MessagesApplication {

    /**
     * Método principal que sirve como punto de entrada a la aplicación.
     * Utiliza SpringApplication.run para lanzar la aplicación.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(MessagesApplication.class, args);
    }
	
}
