package cit.edu.pawfect.match.booking;

import cit.edu.pawfect.match.notification.NotificationService;
import cit.edu.pawfect.match.notification.Notification;
import cit.edu.pawfect.match.entity.Pet;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.repository.PetRepository;
import cit.edu.pawfect.match.repository.UserRepository;
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
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    public Booking createBooking(Booking booking) {
        Optional<Pet> pet = petRepository.findById(booking.getPetId());
        if (pet.isEmpty()) {
            throw new RuntimeException("Pet not found");
        }
        String ownerId = pet.get().getUserId();
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        String ownerEmail = owner.get().getEmail();
        booking.setStatus("PENDING");
        Booking savedBooking = bookingRepository.save(booking);

        Notification notification = new Notification();
        notification.setUserId(ownerEmail);
        notification.setType("BOOKING_REQUEST");
        notification.setMessage("New booking request for your pet: " + booking.getTitle());
        notification.setLink(savedBooking.getBookingId());
        notificationService.createNotification(notification);

        return savedBooking;
    }

    public Booking approveBooking(String bookingId, String ownerEmail) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        Optional<User> owner = userRepository.findByEmail(ownerEmail);
        if (owner.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        String ownerId = owner.get().getUserID();
        Optional<Pet> pet = petRepository.findById(booking.getPetId());
        if (pet.isEmpty() || !pet.get().getUserId().equals(ownerId)) {
            throw new IllegalArgumentException("Unauthorized: User is not the pet owner");
        }
        booking.setStatus("CONFIRMED");
        Booking updatedBooking;
        try {
            updatedBooking = bookingRepository.save(booking);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save booking: " + e.getMessage());
        }

        // Delete the BOOKING_REQUEST notification
        try {
            notificationService.deleteNotificationByLink(bookingId, ownerEmail);
        } catch (Exception e) {
            System.err.println("Failed to delete BOOKING_REQUEST notification: " + e.getMessage());
        }

        // Notify requester if found
        Optional<User> requester = userRepository.findById(booking.getUserId());
        if (requester.isPresent()) {
            String requesterEmail = requester.get().getEmail();
            Notification notification = new Notification();
            notification.setUserId(requesterEmail);
            notification.setType("BOOKING_APPROVED");
            notification.setMessage("Your booking for " + booking.getTitle() + " has been approved!");
            notification.setLink(bookingId);
            notificationService.createNotification(notification);
        } else {
            System.err.println("Requester notemployees found for userId: " + booking.getUserId() + "; skipping notification");
        }

        return updatedBooking;
    }

    public Booking rejectBooking(String bookingId, String ownerEmail) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        Optional<User> owner = userRepository.findByEmail(ownerEmail);
        if (owner.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        String ownerId = owner.get().getUserID();
        Optional<Pet> pet = petRepository.findById(booking.getPetId());
        if (pet.isEmpty() || !pet.get().getUserId().equals(ownerId)) {
            throw new IllegalArgumentException("Unauthorized: User is not the pet owner");
        }
        booking.setStatus("REJECTED");
        Booking updatedBooking;
        try {
            updatedBooking = bookingRepository.save(booking);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save booking: " + e.getMessage());
        }

        // Delete the BOOKING_REQUEST notification
        try {
            notificationService.deleteNotificationByLink(bookingId, ownerEmail);
        } catch (Exception e) {
            System.err.println("Failed to delete BOOKING_REQUEST notification: " + e.getMessage());
        }

        // Notify requester if found
        Optional<User> requester = userRepository.findById(booking.getUserId());
        if (requester.isPresent()) {
            String requesterEmail = requester.get().getEmail();
            Notification notification = new Notification();
            notification.setUserId(requesterEmail);
            notification.setType("BOOKING_REJECTED");
            notification.setMessage("Your booking for " + booking.getTitle() + " was rejected.");
            notification.setLink(bookingId);
            notificationService.createNotification(notification);
        } else {
            System.err.println("Requester not found for userId: " + booking.getUserId() + "; skipping notification");
        }

        return updatedBooking;
    }

    public List<Booking> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingHistory(String userId) {
        List<Booking> requesterBookings = bookingRepository.findByUserId(userId);
        List<Pet> userPets = petRepository.findByUserId(userId);
        List<String> petIds = userPets.stream().map(Pet::getPetId).collect(Collectors.toList());
        List<Booking> ownerBookings = bookingRepository.findByPetIdIn(petIds);
        return Stream.concat(requesterBookings.stream(), ownerBookings.stream())
            .distinct()
            .collect(Collectors.toList());
    }
}