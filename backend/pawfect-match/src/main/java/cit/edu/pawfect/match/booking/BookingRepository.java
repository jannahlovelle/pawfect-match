package cit.edu.pawfect.match.booking;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByUserId(String userId);
    List<Booking> findByPetIdIn(List<String> petIds);
}
