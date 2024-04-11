package mx.unam.fi.distributed.messages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.node.Node;
import mx.unam.fi.distributed.messages.storage.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class MessagesApplication implements CommandLineRunner {


	private final MessageRepository messageRepository;
	private final Map<String, Node> hosts;

	@Value("${HOST}")
	private String currentHost;

	private static final Scanner sc = new Scanner(System.in);
	public static final BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
		SpringApplication.run(MessagesApplication.class, args);
	}

	private void sendMessage() {
		System.out.print(" * Write the host: ");
		var host = sc.nextLine().trim();

		if (hosts.containsKey(host)) {
			System.out.print(" * Write the message: ");
			var message = sc.nextLine().trim();

			System.out.printf("Sending [%s] to [%s]\n", message, host);
			var messageObj = new Message(currentHost, message, LocalDateTime.now());
			var responseObj = new Client().sendMessage(hosts.get(host), messageObj).orElseThrow();
			messageRepository.saveMessage(messageObj);
			messageRepository.saveMessage(responseObj);

			System.out.printf("Response from server was [%s] at [%s]\n", responseObj.message(), responseObj.timestamp());
		} else {
			System.out.println("Unable to find that node");
		}
	}

	private void showPreviousMessages() {
		var messages = messageRepository.getMessages();

		if (messages.iterator().hasNext()) {
			messages.forEach(m -> {
				System.out.println("{");
				System.out.printf("\t'from': '%s'\n", m.from());
				System.out.printf("\t'message': '%s'\n", m.message());
				System.out.printf("\t'timestamp': '%s'\n", m.timestamp());
				System.out.println("}");
			});
		} else {
			System.out.println("[There are no messages]");
		}
	}

	private void showIncomingMessages() {
		incomingMessages.clear();

		var thread = new Thread(() -> {
			while (true) {
                Message message = null;
                try {
                    message = incomingMessages.take();
                } catch (InterruptedException ignored) {
					log.info("Finishing operation...");
					break;
				} finally {
					if (message != null) {
						System.out.printf("  - New incoming message: [%s] at [%s]\n", message.message(), message.timestamp().toString());
					}
				}
			}
		});

		thread.start();

		sc.nextLine();

		thread.interrupt();
	}

	@Override
	public void run(String... args) {

		System.out.println("Welcome to the messages application");
		boolean hasFinished = false;

		do {
			System.out.println("SELECT AN OPTION...");
			System.out.println("1. Send message");
			System.out.println("2. Show incoming messages");
			System.out.println("3. Show previous messages");
			System.out.println("4. Exit");
			System.out.print("> ");

			var option = sc.nextLine().trim();

			switch (option) {
				case "1":
					sendMessage();
					break;
				case "2":
					showIncomingMessages();
					break;
				case "3":
					showPreviousMessages();
					break;
				case "4":
					hasFinished = true;
					break;
				default:
					System.out.println("Invalid option");
			}
		} while (!hasFinished);
	}
}
