package mx.unam.fi.distributed.messages.messages;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representa un mensaje en el sistema distribuido.
 * Implementa Serializable para permitir la serialización del mensaje.
 * 
 * @param from      El ID del nodo remitente del mensaje
 * @param message   El contenido del mensaje
 * @param timestamp La marca de tiempo en la que se creó el mensaje
 */
public record Message(int from, String message, LocalDateTime timestamp) implements Serializable {}
