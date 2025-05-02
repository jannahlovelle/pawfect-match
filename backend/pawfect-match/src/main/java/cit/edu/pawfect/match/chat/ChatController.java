package cit.edu.pawfect.match.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/thread/{threadId}")
    public void sendMessage(@DestinationVariable String threadId, @RequestBody ChatMessage message, Principal principal) {
        chatService.sendMessage(threadId, message.getContent(), principal);
    }

    @PostMapping("/threads")
    public ResponseEntity<ChatThread> createThread(@RequestBody List<String> participantEmails) {
        System.out.println("POST /api/chat/threads with participants: " + participantEmails);
        ChatThread thread = chatService.createThread(participantEmails);
        return ResponseEntity.ok(thread);
    }

    @GetMapping("/threads")
    public ResponseEntity<List<ChatThread>> getThreads(Principal principal) {
        String email = principal.getName();
        System.out.println("GET /api/chat/threads for email: " + email);
        List<ChatThread> threads = chatService.getThreads(email);
        System.out.println("Returning " + threads.size() + " threads: " + threads);
        return ResponseEntity.ok(threads);
    }

    @GetMapping("/threads/{threadId}")
    public ResponseEntity<ChatThread> getThread(@PathVariable String threadId) {
        System.out.println("GET /api/chat/threads/" + threadId);
        ChatThread thread = chatService.getThreadById(threadId);
        return ResponseEntity.ok(thread);
    }

    @GetMapping("/threads/{threadId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable String threadId) {
        List<ChatMessage> messages = chatService.getMessages(threadId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/threads/{threadId}/read")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable String threadId, Principal principal) {
        chatService.markMessagesAsRead(threadId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test-send/{threadId}")
    public ResponseEntity<ChatMessage> testSendMessage(@PathVariable String threadId, @RequestBody ChatMessage message, Principal principal) {
        return ResponseEntity.ok(chatService.sendMessage(threadId, message.getContent(), principal));
    }
}