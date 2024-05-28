package mx.unam.fi.distributed.messages.server;

import java.io.IOException;

/**
 * Interface para la implementación de un servidor de mensajes en un sistema distribuido.
 * Provee métodos para escuchar, detener y verificar el estado del servidor.
 */
public interface IMessageServer extends Runnable {

    /**
     * Método para comenzar a escuchar las conexiones entrantes.
     * @throws IOException si ocurre un error de entrada/salida al iniciar el servidor.
     */
    void listen() throws IOException;

    /**
     * Método para detener el servidor de mensajes.
     * @throws IOException si ocurre un error de entrada/salida al detener el servidor.
     */
    void kill() throws IOException;

    /**
     * Verifica si el servidor está activo.
     * @return true si el servidor está activo, false en caso contrario.
     */
    boolean isAlive();
}
