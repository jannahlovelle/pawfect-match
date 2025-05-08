package cit.edu.pawfect.match.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ResponseEntity<Booking> approveBooking(@PathVariable String bookingId, Authentication authentication) {
        String ownerId = authentication.getName();
        Booking updatedBooking = bookingService.approveBooking(bookingId, ownerId);
        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/{bookingId}/reject")
    public ResponseEntity<Booking> rejectBooking(@PathVariable String bookingId, Authentication authentication) {
        String ownerId = authentication.getName();
        Booking updatedBooking = bookingService.rejectBooking(bookingId, ownerId);
        return ResponseEntity.ok(updatedBooking);
    }
}