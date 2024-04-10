package mx.unam.fi.distributed.messages.storage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MessageOutputStream extends ObjectOutputStream {
    public MessageOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    public void writeStreamHeader() throws IOException {
    }
}
