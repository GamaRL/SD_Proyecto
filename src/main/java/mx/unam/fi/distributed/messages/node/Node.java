package mx.unam.fi.distributed.messages.node;

public record Node(
        String name,
        String host,
        int id,
        int port
) {}
