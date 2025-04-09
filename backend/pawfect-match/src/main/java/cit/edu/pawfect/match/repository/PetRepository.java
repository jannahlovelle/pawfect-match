package cit.edu.pawfect.match.repository;

import cit.edu.pawfect.match.entity.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends MongoRepository<Pet, String> {

    List<Pet> findByUserId(String userId);
}