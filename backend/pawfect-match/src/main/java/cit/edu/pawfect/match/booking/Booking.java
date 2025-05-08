package cit.edu.pawfect.match.booking;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;

@Document(collection = "bookings")
@Data
public class Booking {
    @Id
    private String bookingId;
    private String userId; // Reference to User
    private String petId; // Reference to Pet
    private LocalDateTime date; // Booking date and time
    private String title; // e.g., "Breeding Appointment"
    private String status; // e.g., "PENDING", "CONFIRMED"
}