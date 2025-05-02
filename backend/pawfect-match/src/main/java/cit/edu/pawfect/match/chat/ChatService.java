package cit.edu.pawfect.match.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private ChatThreadRepository threadRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public ChatMessage sendMessage(String threadId, String content, Principal principal) {
        String senderEmail = principal.getName();
        System.out.println("Sending message from " + senderEmail + " in thread " + threadId);

        ChatMessage message = new ChatMessage(
            UUID.randomUUID().toString(),
            threadId,
            senderEmail,
            content,
            Instant.now(),
            "sent"
        );
        messageRepository.save(message);

        ChatThread thread = threadRepository.findById(threadId)
            .orElseThrow(() -> new RuntimeException("Thread not found: " + threadId));
        thread.getUnreadCounts().forEach((email, count) -> {
            if (!email.equals(senderEmail)) {
                thread.getUnreadCounts().put(email, count + 1);
            }
        });
        threadRepository.save(thread);

        messagingTemplate.convertAndSend("/topic/thread/" + threadId, message);

        return message;
    }

    public ChatThread createThread(List<String> participantEmails) {
        System.out.println("Creating thread with participants: " + participantEmails);
        String threadId = UUID.randomUUID().toString();
        HashMap<String, Integer> unreadCounts = new HashMap<>();
        participantEmails.forEach(email -> unreadCounts.put(email, 0));
        ChatThread thread = new ChatThread(threadId, participantEmails, unreadCounts);
        ChatThread savedThread = threadRepository.save(thread);
        System.out.println("Created thread: " + savedThread.getThreadId());
        return savedThread;
    }

    public ChatThread getThreadById(String threadId) {
        System.out.println("Fetching thread by ID: " + threadId);
        return threadRepository.findById(threadId)
            .orElseThrow(() -> new RuntimeException("Thread not found: " + threadId));
    }

    public List<ChatMessage> getMessages(String threadId) {
        return messageRepository.findByThreadId(threadId);
    }

    public List<ChatThread> getThreads(String email) {
        System.out.println("Fetching threads for email: " + email);
        List<ChatThread> threads = threadRepository.findByParticipantIdsContaining(email);
        System.out.println("Found " + threads.size() + " threads: " + threads);
        return threads;
    }

    public void markMessagesAsRead(String threadId, String email) {
        System.out.println("Marking messages as read for " + email + " in thread " + threadId);
        ChatThread thread = threadRepository.findById(threadId)
            .orElseThrow(() -> new RuntimeException("Thread not found: " + threadId));
        thread.getUnreadCounts().put(email, 0);
        threadRepository.save(thread);
    }
}