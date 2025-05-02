package cit.edu.pawfect.match.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByThreadId(String threadId);

    void deleteBySentAtBefore(Instant cutoff);
}