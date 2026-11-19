package prog5121poe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    private static final Path MESSAGE_FILE = Path.of("messages.json");

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Welcome to QuickChat.");
            System.out.print("Enter First Name: ");
            String firstName = input.nextLine().trim();
            System.out.print("Enter Last Name: ");
            String lastName = input.nextLine().trim();
            System.out.print("Create Username: ");
            String username = input.nextLine().trim();
            System.out.print("Create Password: ");
            String password = input.nextLine();
            System.out.print("Enter Cellphone (+27XXXXXXXXX): ");
            String phone = input.nextLine().trim();

            Login login = new Login(firstName, lastName, username, password, phone);
            String registration = login.registerUser();
            System.out.println(registration);
            if (!registration.equals("User has been registered successfully.")) return;

            System.out.println("\nLOGIN");
            System.out.print("Username: ");
            String loginUsername = input.nextLine().trim();
            System.out.print("Password: ");
            String loginPassword = input.nextLine();
            System.out.println(login.returnLoginStatus(loginUsername, loginPassword));
            if (!login.loginUser(loginUsername, loginPassword)) return;

            System.out.print("How many messages would you like to enter? ");
            int messageLimit = readNonNegativeInt(input);
            MessageManager manager;
            try { manager = new MessageManager(MESSAGE_FILE); }
            catch (IllegalStateException ex) {
                System.out.println(ex.getMessage());
                return;
            }

            int messagesCreated = 0;
            boolean running = true;
            while (running) {
                System.out.println("\nMAIN MENU");
                System.out.println("1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                System.out.println("4) Stored Messages");
                System.out.print("Choose an option: ");
                switch (input.nextLine().trim()) {
                    case "1" -> {
                        if (messagesCreated >= messageLimit) {
                            System.out.println("You have reached the number of messages you selected.");
                            break;
                        }
                        Message message = createMessage(input, messagesCreated, phone);
                        if (!message.isRecipientValid() || message.getMessageText().length() > 250) break;
                        System.out.println("\n1) Send Message\n2) Disregard Message\n3) Store Message to send later");
                        System.out.print("Choose an option: ");
                        String action = input.nextLine().trim();
                        System.out.println(message.SentMessage(action));
                        try {
                            if (action.equals("1")) {
                                manager.addSentMessage(message);
                                System.out.println(message.printMessage());
                            } else if (action.equals("2")) {
                                manager.addDisregardedMessage(message);
                                System.out.println("Message disregarded.");
                            } else if (action.equals("3")) {
                                manager.addStoredMessage(message);
                                System.out.println("Message stored successfully in " + MESSAGE_FILE.toAbsolutePath());
                            }
                        } catch (IOException ex) {
                            System.out.println("Could not update messages.json: " + ex.getMessage());
                        }
                        messagesCreated++;
                    }
                    case "2" -> {
                        if (manager.getSentMessages().isEmpty()) System.out.println("No messages have been sent yet.");
                        else manager.getSentMessages().forEach(m -> System.out.println(m.printMessage() + "\n"));
                        System.out.println("Total messages sent: " + manager.getSentMessages().size());
                    }
                    case "3" -> running = false;
                    case "4" -> storedMessagesMenu(input, manager);
                    default -> System.out.println("Invalid option. Please choose 1, 2, 3 or 4.");
                }
            }
            System.out.println("Total messages sent: " + manager.getSentMessages().size());
            System.out.println("Thank you for using QuickChat.");
        }
    }

    private static Message createMessage(Scanner input, int messageNumber, String sender) {
        System.out.print("Enter recipient cell number (+27 followed by 9 digits): ");
        String recipient = input.nextLine().trim();
        System.out.print("Enter your message (maximum 250 characters): ");
        String text = input.nextLine();
        Message message = new Message(messageNumber, sender, recipient, text, null);
        System.out.println("Message ID generated: " + message.getMessageID());
        System.out.println(message.checkRecipientCell());
        System.out.println(message.validateMessage());
        return message;
    }

    private static void storedMessagesMenu(Scanner input, MessageManager manager) {
        boolean back = false;
        while (!back) {
            System.out.println("\nSTORED MESSAGES");
            System.out.println("1) Display sender and recipient of stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message using its ID");
            System.out.println("4) Search for messages by recipient");
            System.out.println("5) Delete a stored message using its hash");
            System.out.println("6) Display message report");
            System.out.println("7) Back to main menu");
            System.out.print("Choose an option: ");
            switch (input.nextLine().trim()) {
                case "1" -> System.out.println(manager.displayStoredSendersAndRecipients());
                case "2" -> System.out.println(manager.getLongestStoredMessage());
                case "3" -> {
                    System.out.print("Enter message ID: ");
                    System.out.println(manager.searchByMessageID(input.nextLine().trim()));
                }
                case "4" -> {
                    System.out.print("Enter recipient number: ");
                    System.out.println(manager.searchByRecipient(input.nextLine().trim()));
                }
                case "5" -> {
                    System.out.print("Enter message hash: ");
                    try { System.out.println(manager.deleteByHash(input.nextLine().trim())); }
                    catch (IOException ex) { System.out.println("Could not delete message: " + ex.getMessage()); }
                }
                case "6" -> System.out.println(manager.generateReport());
                case "7" -> back = true;
                default -> System.out.println("Invalid option. Please choose 1 to 7.");
            }
        }
    }

    private static int readNonNegativeInt(Scanner input) {
        while (true) {
            try {
                int count = Integer.parseInt(input.nextLine().trim());
                if (count >= 0) return count;
            } catch (NumberFormatException ignored) { }
            System.out.print("Please enter a whole number of 0 or more: ");
        }
    }
}
