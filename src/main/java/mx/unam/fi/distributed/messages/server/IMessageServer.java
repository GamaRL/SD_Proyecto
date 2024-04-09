package mx.unam.fi.distributed.messages.server;

import java.io.IOException;

public interface IMessageServer extends Runnable {
    void listen() throws IOException;
    void kill() throws IOException;
    boolean isAlive();
}
