package mx.unam.fi.distributed.messages.node;

/**
 * Representa un nodo en el sistema distribuido.
 */
public record Node(
        String name,    // Nombre del nodo
        String host,    // Direccion IP
        int id,         // ID unico
        int port        // Puerto       
) {}
