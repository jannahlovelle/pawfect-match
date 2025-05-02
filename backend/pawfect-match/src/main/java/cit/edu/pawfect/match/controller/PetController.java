package cit.edu.pawfect.match.controller;

import cit.edu.pawfect.match.dto.CreatePetRequest;
import cit.edu.pawfect.match.dto.PetFeedResponse;
import cit.edu.pawfect.match.dto.UpdatePetRequest;
import cit.edu.pawfect.match.entity.Pet;
import cit.edu.pawfect.match.entity.Photo;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.repository.UserRepository;
import cit.edu.pawfect.match.service.PetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pets")
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    @Autowired
    private PetService petService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createPet(@Valid @RequestBody CreatePetRequest petRequest) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", email);
                        return new RuntimeException("User not found with email: " + email);
                    });

            Pet savedPet = petService.createPet(user.getUserID(), petRequest);
            Map<String, String> response = new HashMap<>();
            response.put("petId", savedPet.getPetId());
            response.put("message", "Pet created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating pet: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while creating the pet: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PostMapping("/{petId}/photos")
    public ResponseEntity<?> addPhoto(@PathVariable String petId, @RequestParam("file") MultipartFile file) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            validateFile(file);
            Photo savedPhoto = petService.addPhoto(petId, email, file);
            Map<String, String> response = new HashMap<>();
            response.put("photoId", savedPhoto.getPhotoId());
            response.put("url", savedPhoto.getUrl());
            response.put("message", "Photo added successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding photo for petId: {}: {}", petId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while adding the photo: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/photos/{photoId}")
    public ResponseEntity<?> updatePhoto(@PathVariable String photoId, @RequestParam("file") MultipartFile file) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            validateFile(file);
            Photo updatedPhoto = petService.updatePetPhoto(photoId, email, file);
            Map<String, String> response = new HashMap<>();
            response.put("photoId", updatedPhoto.getPhotoId());
            response.put("url", updatedPhoto.getUrl());
            response.put("message", "Photo updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating photo with ID: {}: {}", photoId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while updating the photo: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable String photoId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            petService.deletePetPhoto(photoId, email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Photo deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting photo with ID: {}: {}", photoId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while deleting the photo: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.error("File upload failed: File is empty");
            throw new IllegalArgumentException("File is empty. Please upload a valid file.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            logger.error("File upload failed: Invalid file type - {}", contentType);
            throw new IllegalArgumentException("Invalid file type. Only JPEG and PNG files are allowed.");
        }

        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxFileSize) {
            logger.error("File upload failed: File size exceeds 5MB - {} bytes", file.getSize());
            throw new IllegalArgumentException("File size exceeds the limit of 5MB.");
        }

        logger.info("File validation passed: type={}, size={} bytes", contentType, file.getSize());
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }

    @GetMapping("/my-pets")
    public ResponseEntity<?> getMyPets() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", email);
                        return new RuntimeException("User not found with email: " + email);
                    });

            List<Pet> pets = petService.getPetsByUserId(user.getUserID());
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            logger.error("Error retrieving pets: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while retrieving pets: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @GetMapping("/{petId}/photos")
    public ResponseEntity<?> getPetPhotos(@PathVariable String petId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", email);
                        return new RuntimeException("User not found with email: " + email);
                    });

            petService.getPetsByUserId(user.getUserID()).stream()
                    .filter(p -> p.getPetId().equals(petId))
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.error("Pet not found or unauthorized: {}", petId);
                        return new RuntimeException("Pet not found or does not belong to the user");
                    });

            List<Photo> photos = petService.getPhotosByPetId(petId);
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            logger.error("Error retrieving photos for petId: {}: {}", petId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while retrieving photos: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/update/{petId}")
    public ResponseEntity<?> updatePet(@PathVariable String petId, @Valid @RequestBody UpdatePetRequest request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                logger.error("Pet name is required for petId: {}", petId);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Pet name is required");
                return ResponseEntity.status(400).body(errorResponse);
            }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", email);
                        return new RuntimeException("User not found with email: " + email);
                    });

            Pet updatedPet = petService.updatePet(petId, user.getUserID(), request);
            Map<String, String> response = new HashMap<>();
            response.put("petId", updatedPet.getPetId());
            response.put("message", "Pet updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating petId: {}: {}", petId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while updating the pet: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{petId}")
    public ResponseEntity<?> deletePet(@PathVariable String petId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", email);
                        return new RuntimeException("User not found with email: " + email);
                    });

            petService.deletePet(petId, user.getUserID());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Pet deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting petId: {}: {}", petId, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while deleting the pet: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getPetFeed(
            @RequestParam(value = "species", required = false) String species,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (email == null || email.equals("anonymousUser")) {
                logger.error("Unauthorized access to pet feed");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Authentication required to access the pet feed");
                return ResponseEntity.status(401).body(errorResponse);
            }
            List<PetFeedResponse> feed = petService.getPetsForFeed(species, page, size);
            return ResponseEntity.ok(feed);
        } catch (Exception e) {
            logger.error("Error retrieving pet feed: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while retrieving the pet feed: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
}