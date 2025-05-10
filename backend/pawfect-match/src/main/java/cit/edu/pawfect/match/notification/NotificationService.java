package cit.edu.pawfect.match.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        Notification savedNotification = notificationRepository.save(notification);

        try {
            String userId = savedNotification.getUserId();
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/notifications",
                    savedNotification
            );
            System.out.println("Sent notification to user " + userId + ": " + savedNotification.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket notification: " + e.getMessage());
        }

        return savedNotification;
    }

    public List<Notification> getNotificationsByUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void deleteNotificationByLink(String link, String userId) {
        notificationRepository.deleteByLinkAndUserId(link, userId);
    }
}