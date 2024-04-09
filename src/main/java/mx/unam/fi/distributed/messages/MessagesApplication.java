package mx.unam.fi.distributed.messages;

import lombok.extern.slf4j.Slf4j;
import mx.unam.fi.distributed.messages.client.Client;
import mx.unam.fi.distributed.messages.messages.Message;
import mx.unam.fi.distributed.messages.node.Node;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
@Slf4j
public class MessagesApplication implements CommandLineRunner {

	private static final Scanner sc = new Scanner(System.in);
	public static final BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
		SpringApplication.run(MessagesApplication.class, args);
	}

	private void sendMessage() {
		System.out.print(" * Write the host: ");
		var host = sc.nextLine().trim();
		System.out.print(" * Write the message: ");
		var message = sc.nextLine().trim();

		System.out.printf("Sending [%s] to [%s]\n", message, host);
		var messageObj = new Message(message, LocalDateTime.now());
		var responseObj = new Client().sendMessage(new Node(host, host, 5000), messageObj).orElseThrow();

		System.out.printf("Response from server was [%s] at [%s]\n", responseObj.message(), responseObj.timestamp());
	}

	private void showPreviousMessages() {
		System.out.println("[There are no messages]");
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
				} finally {
					if (message != null) {
						System.out.printf("  - New incoming message: [%s] at [%s]", message.message(), message.timestamp().toString());
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
