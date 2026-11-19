package prog5121poe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class MessageManagerTest {
    @TempDir Path tempDir;

    private MessageManager managerWithTestData() throws Exception {
        Path file = tempDir.resolve("messages.json");
        MessageManager manager = new MessageManager(file);

        Message message1 = new Message(0, "Developer", "+27834557896",
                "Did you get the cake?", "1111111111");
        Message message2 = new Message(1, "Developer", "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", "2222222222");
        Message message3 = new Message(2, "Developer", "+27834484567",
                "Yohoooo, I am at your gate.", "3333333333");
        Message message4 = new Message(3, "Developer", "0838884567",
                "It is dinner time!", "4444444444");
        Message message5 = new Message(4, "Developer", "+27838884567",
                "Ok, I am leaving without you.", "5555555555");

        manager.addSentMessage(message1);
        manager.addStoredMessage(message2);
        manager.addDisregardedMessage(message3);
        manager.addSentMessage(message4);
        manager.addStoredMessage(message5);
        return manager;
    }

    @Test
    void sentMessagesArrayIsCorrectlyPopulated() throws Exception {
        MessageManager manager = managerWithTestData();
        assertEquals(2, manager.getSentMessagesArray().length);
        assertEquals("Did you get the cake?", manager.getSentMessagesArray()[0]);
        assertEquals("It is dinner time!", manager.getSentMessagesArray()[1]);
    }

    @Test
    void longestStoredMessageIsDisplayed() throws Exception {
        MessageManager manager = managerWithTestData();
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                manager.getLongestStoredMessage());
    }

    @Test
    void searchForMessageIDReturnsRecipientAndMessage() throws Exception {
        MessageManager manager = managerWithTestData();
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                manager.searchByMessageID("4444444444"));
    }

    @Test
    void searchForRecipientReturnsAllMatchingStoredAndSentMessages() throws Exception {
        MessageManager manager = managerWithTestData();
        assertEquals("Where are you? You are late! I have asked you to be on time.\n"
                        + "Ok, I am leaving without you.",
                manager.searchByRecipient("+27838884567"));
    }

    @Test
    void deleteMessageUsingHashRemovesItFromStoredArrayAndJson() throws Exception {
        MessageManager manager = managerWithTestData();
        String hash = manager.getStoredMessages().get(0).getMessageHash();
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                manager.deleteByHash(hash));
        assertEquals(1, manager.getStoredMessagesArray().length);
        assertEquals("Ok, I am leaving without you.", manager.getStoredMessagesArray()[0]);
    }

    @Test
    void displayReportListsMessageHashRecipientAndMessage() throws Exception {
        MessageManager manager = managerWithTestData();
        String expected = "Message Hash: 11:0:DIDCAKE\n"
                + "Recipient: +27834557896\nMessage: Did you get the cake?\n"
                + "--------------------\n"
                + "Message Hash: 44:3:ITTIME\n"
                + "Recipient: 0838884567\nMessage: It is dinner time!\n"
                + "--------------------\n"
                + "Message Hash: 22:1:WHERETIME\n"
                + "Recipient: +27838884567\nMessage: Where are you? You are late! I have asked you to be on time.\n"
                + "--------------------\n"
                + "Message Hash: 55:4:OKYOU\n"
                + "Recipient: +27838884567\nMessage: Ok, I am leaving without you.";
        assertEquals(expected, manager.generateReport());
    }

    @Test
    void storedMessagesAreLoadedBackFromJsonIntoArray() throws Exception {
        MessageManager manager = managerWithTestData();
        MessageManager loaded = new MessageManager(tempDir.resolve("messages.json"));
        assertEquals(2, loaded.getStoredMessagesArray().length);
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                loaded.getStoredMessagesArray()[0]);
    }
}
