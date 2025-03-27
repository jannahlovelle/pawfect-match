package cit.edu.pawfect.match.repository;

import cit.edu.pawfect.match.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email); // Added
    boolean existsByEmail(String email); // Added
}