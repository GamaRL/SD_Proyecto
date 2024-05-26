package mx.unam.fi.distributed.messages.storage;

import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.messages.Message;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Repository
@Slf4j
public class MessageRepository {

    private final Lock lock = new ReentrantLock();

    public synchronized void saveMessage(Message message) {
        var messagesFile = new File("messages.ser");
        lock.lock();
        try (
                var fileOutputStream = new FileOutputStream("messages.ser", true)
        ) {
            if (messagesFile.length() == 0) {
                var objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(message);
                objectOutputStream.close();
            } else {
                var objectOutputStream = new MessageOutputStream(fileOutputStream);
                objectOutputStream.writeObject(message);
                objectOutputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lock.unlock();
    }

    public synchronized Iterable<Message> getMessages() {
        lock.lock();

        var messagesList = new ArrayList<Message>();
        try (
                var fileInputStream = new FileInputStream("messages.ser");
                var objectInputStream = new ObjectInputStream(fileInputStream)
        ) {
            Message message;

            while (fileInputStream.available() != 0) {
                message = (Message)objectInputStream.readObject();
                messagesList.add(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage());
        }

        lock.unlock();

        return messagesList;
    }
}
