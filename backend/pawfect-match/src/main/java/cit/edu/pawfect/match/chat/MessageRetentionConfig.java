package cit.edu.pawfect.match.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@EnableScheduling
public class MessageRetentionConfig {

    @Autowired
    private ChatMessageRepository messageRepository;

    // Run daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldMessages() {
        Instant cutoff = Instant.now().minus(90, ChronoUnit.DAYS);
        messageRepository.deleteBySentAtBefore(cutoff);
    }
}
