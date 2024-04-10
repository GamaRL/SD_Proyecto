package mx.unam.fi.distributed.messages.messages;

import java.io.Serializable;
import java.time.LocalDateTime;

public record Message(String from, String message, LocalDateTime timestamp) implements Serializable {
}
