package cit.edu.pawfect.match.repository;

import cit.edu.pawfect.match.entity.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends MongoRepository<Pet, String> {

    List<Pet> findByUserId(String userId);

    @Query("{ 'availabilityStatus': ?0 }")
    List<Pet> findByAvailabilityStatus(String availabilityStatus);

    @Query("{ 'species': ?0, 'availabilityStatus': ?1 }")
    List<Pet> findBySpeciesAndAvailabilityStatus(String species, String availabilityStatus);
}