package mx.unam.fi.distributed.messages.node;

/**
 * Representa un nodo en el sistema distribuido.
 */
public record Node(
        String name,    // Nombre del nodo
        String host,    // Dirección IP
        int id,         // ID único
        int port        // Puerto       
) {}
