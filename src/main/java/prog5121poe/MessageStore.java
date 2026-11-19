package prog5121poe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes the message array in messages.json. */
public final class MessageStore {
    private MessageStore() { }

    public static void storeMessage(Message message, Path file) throws IOException {
        List<Message> messages = loadMessages(file);
        messages.add(message);
        saveMessages(messages, file);
    }

    public static void saveMessages(List<Message> messages, Path file) throws IOException {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            json.append("  {\n")
                .append("    \"messageID\": \"").append(escape(m.getMessageID())).append("\",\n")
                .append("    \"messageHash\": \"").append(escape(m.getMessageHash())).append("\",\n")
                .append("    \"messageNumber\": ").append(m.getMessageNumber()).append(",\n")
                .append("    \"sender\": \"").append(escape(m.getSender())).append("\",\n")
                .append("    \"recipient\": \"").append(escape(m.getRecipient())).append("\",\n")
                .append("    \"message\": \"").append(escape(m.getMessageText())).append("\"\n")
                .append("  }");
            if (i < messages.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]\n");
        Path absolute = file.toAbsolutePath();
        if (absolute.getParent() != null) Files.createDirectories(absolute.getParent());
        Files.writeString(file, json.toString(), StandardCharsets.UTF_8);
    }

    public static List<Message> loadMessages(Path file) throws IOException {
        List<Message> result = new ArrayList<>();
        if (!Files.exists(file) || Files.readString(file, StandardCharsets.UTF_8).isBlank()) return result;
        String json = Files.readString(file, StandardCharsets.UTF_8);
        List<String> objects = extractObjects(json);
        for (String object : objects) {
            String id = field(object, "messageID");
            String hash = field(object, "messageHash");
            String numberText = field(object, "messageNumber");
            String sender = field(object, "sender");
            String recipient = field(object, "recipient");
            String message = field(object, "message");
            int number;
            try { number = Integer.parseInt(numberText); }
            catch (NumberFormatException ex) { number = result.size(); }
            Message loaded = new Message(number, sender, recipient, message, id);
            // The hash is generated from the same stored ID, message number and text.
            result.add(loaded);
        }
        return result;
    }

    private static List<String> extractObjects(String json) throws IOException {
        List<String> objects = new ArrayList<>();
        boolean inString = false, escaped = false;
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (inString) {
                if (escaped) escaped = false;
                else if (c == '\\') escaped = true;
                else if (c == '"') inString = false;
                continue;
            }
            if (c == '"') inString = true;
            else if (c == '{') { if (depth++ == 0) start = i; }
            else if (c == '}') {
                if (--depth < 0) throw new IOException("Invalid JSON message file.");
                if (depth == 0 && start >= 0) objects.add(json.substring(start, i + 1));
            }
        }
        if (inString || depth != 0 || !json.trim().startsWith("[") || !json.trim().endsWith("]"))
            throw new IOException("Invalid JSON message file.");
        return objects;
    }

    private static String field(String object, String name) throws IOException {
        String key = "\"" + name + "\"";
        int position = object.indexOf(key);
        if (position < 0) return "";
        int colon = object.indexOf(':', position + key.length());
        if (colon < 0) throw new IOException("Invalid JSON field: " + name);
        int i = colon + 1;
        while (i < object.length() && Character.isWhitespace(object.charAt(i))) i++;
        if (i < object.length() && object.charAt(i) == '"') {
            StringBuilder value = new StringBuilder();
            i++;
            boolean escaped = false;
            while (i < object.length()) {
                char c = object.charAt(i++);
                if (escaped) {
                    switch (c) {
                        case 'n' -> value.append('\n'); case 'r' -> value.append('\r');
                        case 't' -> value.append('\t'); case 'b' -> value.append('\b');
                        case 'f' -> value.append('\f'); case '"' -> value.append('"');
                        case '\\' -> value.append('\\'); case '/' -> value.append('/');
                        case 'u' -> {
                            if (i + 4 > object.length()) throw new IOException("Invalid JSON escape.");
                            value.append((char) Integer.parseInt(object.substring(i, i + 4), 16)); i += 4;
                        }
                        default -> value.append(c);
                    }
                    escaped = false;
                } else if (c == '\\') escaped = true;
                else if (c == '"') return value.toString();
                else value.append(c);
            }
            throw new IOException("Unterminated JSON string.");
        }
        int end = i;
        while (end < object.length() && Character.isDigit(object.charAt(end))) end++;
        return object.substring(i, end).trim();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n")
                .replace("\r", "\\r").replace("\t", "\\t");
    }
}
