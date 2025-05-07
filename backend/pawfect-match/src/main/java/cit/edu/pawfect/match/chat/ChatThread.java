package cit.edu.pawfect.match.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "chat_threads")
public class ChatThread {

    @Id
    private String threadId;
    private List<String> participantIds; // List of participant emails (e.g., johncutab@gmail.com)
    private Map<String, Integer> unreadCounts; // Map of email to unread message count

    public ChatThread() {
    }

    public ChatThread(String threadId, List<String> participantIds, Map<String, Integer> unreadCounts) {
        this.threadId = threadId;
        this.participantIds = participantIds;
        this.unreadCounts = unreadCounts;
    }

    // Getters and setters
    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public Map<String, Integer> getUnreadCounts() {
        return unreadCounts != null ? unreadCounts : new HashMap<>();
    }

    public void setUnreadCounts(Map<String, Integer> unreadCounts) {
        this.unreadCounts = unreadCounts;
    }
}
