package prog5121poe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Keeps the message collections required by the QuickChat assignment. */
public class MessageManager {
    private final List<Message> sentMessages = new ArrayList<>();
    private final List<Message> disregardedMessages = new ArrayList<>();
    private final List<Message> storedMessages = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> messageIDs = new ArrayList<>();
    private final Path messageFile;

    public MessageManager(Path messageFile) {
        this.messageFile = messageFile;
        try { storedMessages.addAll(MessageStore.loadMessages(messageFile)); }
        catch (IOException ex) { throw new IllegalStateException("Could not read stored messages: " + ex.getMessage(), ex); }
        for (Message message : storedMessages) addIdentifiers(message);
    }

    public void addSentMessage(Message message) {
        if (!sentMessages.contains(message)) sentMessages.add(message);
        message.markSent(true);
        addIdentifiers(message);
    }

    public void addDisregardedMessage(Message message) {
        if (!disregardedMessages.contains(message)) disregardedMessages.add(message);
        addIdentifiers(message);
    }

    public void addStoredMessage(Message message) throws IOException {
        if (storedMessages.stream().noneMatch(m -> m.getMessageID().equals(message.getMessageID()))) {
            storedMessages.add(message);
            MessageStore.saveMessages(storedMessages, messageFile);
        }
        addIdentifiers(message);
    }

    private void addIdentifiers(Message message) {
        if (!messageIDs.contains(message.getMessageID())) messageIDs.add(message.getMessageID());
        if (!messageHashes.contains(message.getMessageHash())) messageHashes.add(message.getMessageHash());
    }

    public String displayStoredSendersAndRecipients() {
        if (storedMessages.isEmpty()) return "There are no stored messages.";
        StringBuilder output = new StringBuilder();
        for (Message m : storedMessages) {
            output.append("Sender: ").append(m.getSender()).append(" | Recipient: ")
                    .append(m.getRecipient()).append(System.lineSeparator());
        }
        return output.toString().trim();
    }

    public String getLongestStoredMessage() {
        if (storedMessages.isEmpty()) return "There are no stored messages.";
        Message longest = storedMessages.get(0);
        for (Message m : storedMessages) if (m.getMessageText().length() > longest.getMessageText().length()) longest = m;
        return longest.getMessageText();
    }

    public String searchByMessageID(String id) {
        for (Message m : allMessages()) {
            if (m.getMessageID().equals(id)) return "Recipient: " + m.getRecipient() + "\nMessage: " + m.getMessageText();
        }
        return "Message ID not found.";
    }

    public String searchByRecipient(String recipient) {
        StringBuilder output = new StringBuilder();
        for (Message m : allMessages()) {
            if (m.getRecipient().equals(recipient)) {
                if (output.length() > 0) output.append(System.lineSeparator());
                output.append(m.getMessageText());
            }
        }
        return output.length() == 0 ? "No messages found for this recipient." : output.toString();
    }

    public String deleteByHash(String hash) throws IOException {
        Message match = null;
        for (Message m : storedMessages) if (m.getMessageHash().equalsIgnoreCase(hash)) { match = m; break; }
        if (match == null) return "Message hash not found.";
        storedMessages.remove(match);
        MessageStore.saveMessages(storedMessages, messageFile);
        return "Message: \"" + match.getMessageText() + "\" successfully deleted.";
    }

    /** Report covers sent and stored messages so that no completed message is omitted. */
    public String generateReport() {
        List<Message> reportMessages = allMessages();
        if (reportMessages.isEmpty()) return "There are no messages to report.";
        StringBuilder report = new StringBuilder();
        for (Message m : reportMessages) {
            if (report.length() > 0) report.append("\n--------------------\n");
            report.append("Message Hash: ").append(m.getMessageHash())
                    .append("\nRecipient: ").append(m.getRecipient())
                    .append("\nMessage: ").append(m.getMessageText());
        }
        return report.toString();
    }

    private List<Message> allMessages() {
        List<Message> all = new ArrayList<>(sentMessages);
        for (Message m : storedMessages)
            if (all.stream().noneMatch(existing -> existing.getMessageID().equals(m.getMessageID()))) all.add(m);
        return all;
    }

    public String[] getSentMessagesArray() { return sentMessages.stream().map(Message::getMessageText).toArray(String[]::new); }
    public String[] getDisregardedMessagesArray() { return disregardedMessages.stream().map(Message::getMessageText).toArray(String[]::new); }
    public String[] getStoredMessagesArray() { return storedMessages.stream().map(Message::getMessageText).toArray(String[]::new); }
    public String[] getMessageHashesArray() { return messageHashes.toArray(String[]::new); }
    public String[] getMessageIDsArray() { return messageIDs.toArray(String[]::new); }
    public List<Message> getSentMessages() { return List.copyOf(sentMessages); }
    public List<Message> getDisregardedMessages() { return List.copyOf(disregardedMessages); }
    public List<Message> getStoredMessages() { return List.copyOf(storedMessages); }
}
