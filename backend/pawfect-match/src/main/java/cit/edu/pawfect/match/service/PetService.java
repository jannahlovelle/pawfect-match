package cit.edu.pawfect.match.service;

import cit.edu.pawfect.match.dto.CreatePetRequest;
import cit.edu.pawfect.match.dto.UpdatePetRequest;
import cit.edu.pawfect.match.dto.AddPhotoRequest;
import cit.edu.pawfect.match.entity.Pet;
import cit.edu.pawfect.match.entity.Photo;
import cit.edu.pawfect.match.repository.PetRepository;
import cit.edu.pawfect.match.repository.PhotoRepository;
import cit.edu.pawfect.match.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    public Pet createPet(String userId, CreatePetRequest petRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setName(petRequest.getName());
        pet.setSpecies(petRequest.getSpecies());
        pet.setBreed(petRequest.getBreed());
        pet.setGender(petRequest.getGender());
        pet.setDateOfBirth(petRequest.getDateOfBirth());
        pet.setWeight(petRequest.getWeight());
        pet.setColor(petRequest.getColor());
        pet.setDescription(petRequest.getDescription());
        pet.setAvailabilityStatus(petRequest.getAvailabilityStatus());
        pet.setPrice(petRequest.getPrice());
        pet.setPedigreeInfo(petRequest.getPedigreeInfo());
        pet.setHealthStatus(petRequest.getHealthStatus());

        return petRepository.save(pet);
    }

    public Photo addPhoto(String petId, AddPhotoRequest photoRequest) {
        petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + petId));

        Photo photo = new Photo();
        photo.setPetId(petId);
        photo.setUrl(photoRequest.getUrl());

        return photoRepository.save(photo);
    }

    public List<Pet> getPetsByUserId(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return petRepository.findByUserId(userId);
    }

    public List<Photo> getPhotosByPetId(String petId) {
        petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + petId));

        return photoRepository.findByPetId(petId);
    }

    public Pet updatePet(String petId, String userId, UpdatePetRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + petId));

        if (!pet.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this pet");
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            pet.setName(request.getName());
        }
        if (request.getSpecies() != null) {
            pet.setSpecies(request.getSpecies());
        }
        if (request.getBreed() != null) {
            pet.setBreed(request.getBreed());
        }
        if (request.getGender() != null) {
            pet.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            pet.setDateOfBirth(java.sql.Timestamp.valueOf(request.getDateOfBirth()));
        }
        if (request.getWeight() != null) {
            pet.setWeight(request.getWeight());
        }
        if (request.getColor() != null) {
            pet.setColor(request.getColor());
        }
        if (request.getDescription() != null) {
            pet.setDescription(request.getDescription());
        }
        if (request.getAvailabilityStatus() != null) {
            pet.setAvailabilityStatus(request.getAvailabilityStatus());
        }
        if (request.getPrice() != null) {
            pet.setPrice(request.getPrice());
        }
        if (request.getPedigreeInfo() != null) {
            pet.setPedigreeInfo(request.getPedigreeInfo());
        }
        if (request.getHealthStatus() != null) {
            pet.setHealthStatus(request.getHealthStatus());
        }

        return petRepository.save(pet);
    }

    public void deletePet(String petId, String userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + petId));

        if (!pet.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this pet");
        }

        photoRepository.deleteByPetId(petId);
        petRepository.deleteById(petId);
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet adminUpdatePet(String petId, UpdatePetRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + petId));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            pet.setName(request.getName());
        }
        if (request.getSpecies() != null) {
            pet.setSpecies(request.getSpecies());
        }
        if (request.getBreed() != null) {
            pet.setBreed(request.getBreed());
        }
        if (request.getGender() != null) {
            pet.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            pet.setDateOfBirth(java.sql.Timestamp.valueOf(request.getDateOfBirth()));
        }
        if (request.getWeight() != null) {
            pet.setWeight(request.getWeight());
        }
        if (request.getColor() != null) {
            pet.setColor(request.getColor());
        }
        if (request.getDescription() != null) {
            pet.setDescription(request.getDescription());
        }
        if (request.getAvailabilityStatus() != null) {
            pet.setAvailabilityStatus(request.getAvailabilityStatus());
        }
        if (request.getPrice() != null) {
            pet.setPrice(request.getPrice());
        }
        if (request.getPedigreeInfo() != null) {
            pet.setPedigreeInfo(request.getPedigreeInfo());
        }
        if (request.getHealthStatus() != null) {
            pet.setHealthStatus(request.getHealthStatus());
        }

        return petRepository.save(pet);
    }

    public void adminDeletePet(String petId) {
        if (!petRepository.existsById(petId)) {
            throw new RuntimeException("Pet not found with ID: " + petId);
        }

        photoRepository.deleteByPetId(petId);
        petRepository.deleteById(petId);
    }

    public Photo updatePetPhoto(String photoId, String userId, String url) { // Changed from newPhotoUrl to url
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found with ID: " + photoId));

        Pet pet = petRepository.findById(photo.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + photo.getPetId()));

        if (!pet.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this pet's photo");
        }

        photo.setUrl(url);
        return photoRepository.save(photo);
    }

    public void deletePetPhoto(String photoId, String userId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found with ID: " + photoId));

        Pet pet = petRepository.findById(photo.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found with ID: " + photo.getPetId()));

        if (!pet.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this pet's photo");
        }

        photoRepository.delete(photo);
    }

    public Photo adminUpdatePetPhoto(String photoId, String url) { // Changed from newPhotoUrl to url
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found with ID: " + photoId));

        photo.setUrl(url);
        return photoRepository.save(photo);
    }

    public void adminDeletePetPhoto(String photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found with ID: " + photoId));

        photoRepository.delete(photo);
    }
}