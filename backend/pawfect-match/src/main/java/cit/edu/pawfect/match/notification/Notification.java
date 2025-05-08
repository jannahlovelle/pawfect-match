package cit.edu.pawfect.match.notification;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
public class Notification {
    @Id
    private String notificationId;
    private String userId; // Recipient's user ID
    private String type; // e.g., BOOKING_REQUEST, BOOKING_APPROVED, BOOKING_REJECTED
    private String message; // Notification text
    private String link; // Link to booking (e.g., bookingId)
    private boolean read; // Read status
    private LocalDateTime createdAt;
}