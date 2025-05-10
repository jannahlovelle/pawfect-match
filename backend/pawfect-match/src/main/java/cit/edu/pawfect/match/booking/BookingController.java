package cit.edu.pawfect.match.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking, Authentication authentication) {
        String userId = authentication.getName();
        booking.setUserId(userId);
        Booking savedBooking = bookingService.createBooking(booking);
        return ResponseEntity.ok(savedBooking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getBookings(Authentication authentication) {
        String userId = authentication.getName();
        List<Booking> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Booking>> getBookingHistory(Authentication authentication) {
        String userId = authentication.getName();
        List<Booking> bookings = bookingService.getBookingHistory(userId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/{bookingId}/approve")
    public ResponseEntity<?> approveBooking(@PathVariable String bookingId, Authentication authentication) {
        try {
            String ownerEmail = authentication.getName();
            Booking updatedBooking = bookingService.approveBooking(bookingId, ownerEmail);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to approve booking: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable String bookingId, Authentication authentication) {
        try {
            String ownerEmail = authentication.getName();
            Booking updatedBooking = bookingService.rejectBooking(bookingId, ownerEmail);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to reject booking: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}