package cit.edu.pawfect.match.booking;


import cit.edu.pawfect.match.notification.NotificationService;
import cit.edu.pawfect.match.notification.Notification;
import cit.edu.pawfect.match.entity.Pet;
import cit.edu.pawfect.match.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private NotificationService notificationService;

    public Booking createBooking(Booking booking) {
        // Fetch pet to get ownerId (userId from Pet)
        Optional<Pet> pet = petRepository.findById(booking.getPetId());
        if (pet.isEmpty()) {
            throw new RuntimeException("Pet not found");
        }
        String ownerId = pet.get().getUserId();
        booking.setStatus("PENDING");
        Booking savedBooking = bookingRepository.save(booking);

        // Create notification for pet owner
        Notification notification = new Notification();
        notification.setUserId(ownerId);
        notification.setType("BOOKING_REQUEST");
        notification.setMessage("New booking request for your pet: " + booking.getTitle());
        notification.setLink(savedBooking.getBookingId());
        notificationService.createNotification(notification);

        return savedBooking;
    }

    public Booking approveBooking(String bookingId, String ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        // Verify owner
        Optional<Pet> pet = petRepository.findById(booking.getPetId());
        if (pet.isEmpty() || !pet.get().getUserId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }
        booking.setStatus("CONFIRMED");
        Booking updatedBooking = bookingRepository.save(booking);

        // Notify requester
        Notification notification = new Notification();
        notification.setUserId(booking.getUserId());
        notification.setType("BOOKING_APPROVED");
        notification.setMessage("Your booking for " + booking.getTitle() + " has been approved!");
        notification.setLink(bookingId);
        notificationService.createNotification(notification);

        return updatedBooking;
    }

    public Booking rejectBooking(String bookingId, String ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        // Verify owner
        Optional<Pet> pet = petRepository.findById(booking.getPetId());
        if (pet.isEmpty() || !pet.get().getUserId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }
        booking.setStatus("REJECTED");
        Booking updatedBooking = bookingRepository.save(booking);

        // Notify requester
        Notification notification = new Notification();
        notification.setUserId(booking.getUserId());
        notification.setType("BOOKING_REJECTED");
        notification.setMessage("Your booking for " + booking.getTitle() + " was rejected.");
        notification.setLink(bookingId);
        notificationService.createNotification(notification);

        return updatedBooking;
    }

    public List<Booking> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingHistory(String userId) {
        // Get bookings where user is requester
        List<Booking> requesterBookings = bookingRepository.findByUserId(userId);

        // Get bookings where user is owner (via Pet.userId)
        List<Pet> userPets = petRepository.findByUserId(userId);
        List<String> petIds = userPets.stream().map(Pet::getPetId).collect(Collectors.toList());
        List<Booking> ownerBookings = bookingRepository.findByPetIdIn(petIds);

        // Combine and remove duplicates
        return Stream.concat(requesterBookings.stream(), ownerBookings.stream())
            .distinct()
            .collect(Collectors.toList());
    }
}