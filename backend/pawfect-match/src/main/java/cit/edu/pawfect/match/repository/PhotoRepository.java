package cit.edu.pawfect.match.repository;

import cit.edu.pawfect.match.entity.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends MongoRepository<Photo, String> {

    List<Photo> findByPetId(String petId);

    void deleteByPetId(String petId); // To delete photos when a pet is deleted
}