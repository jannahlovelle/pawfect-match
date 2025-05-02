package cit.edu.pawfect.match.chat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatThreadRepository extends MongoRepository<ChatThread, String> {
    @Query("{ 'participantIds': ?0 }")
    List<ChatThread> findByParticipantIdsContaining(String email);

    default List<ChatThread> logFindByParticipantIdsContaining(String email) {
        System.out.println("Querying threads for participant email: " + email);
        List<ChatThread> threads = findByParticipantIdsContaining(email);
        System.out.println("Found " + threads.size() + " threads for " + email + ": " + threads);
        return threads;
    }
}
