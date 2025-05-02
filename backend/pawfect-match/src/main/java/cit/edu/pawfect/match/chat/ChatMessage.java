
package cit.edu.pawfect.match.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String messageId;
    private String threadId;
    private String senderEmail; // Email of the sender (e.g., johncutab@gmail.com)
    private String content;
    private Instant sentAt;
    private String status;

    public ChatMessage() {
    }

    public ChatMessage(String messageId, String threadId, String senderEmail, String content, Instant sentAt, String status) {
        this.messageId = messageId;
        this.threadId = threadId;
        this.senderEmail = senderEmail;
        this.content = content;
        this.sentAt = sentAt;
        this.status = status;
    }

    // Getters and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
