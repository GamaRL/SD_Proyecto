package mx.unam.fi.distributed.messages.client;

import mx.unam.fi.distributed.messages.node.Node;

import java.util.Optional;

public interface IClient {
    Optional<String> sendMessage(Node destination, String message);
}
