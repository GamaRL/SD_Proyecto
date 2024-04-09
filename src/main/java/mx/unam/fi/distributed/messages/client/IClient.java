package mx.unam.fi.distributed.messages.client;

import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.node.Node;

import java.util.Optional;

public interface IClient {
    Optional<Message> sendMessage(Node destination, Message message);
}
