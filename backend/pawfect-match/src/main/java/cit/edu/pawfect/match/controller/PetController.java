package cit.edu.pawfect.match.controller;

import cit.edu.pawfect.match.dto.CreatePetRequest;
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
            // Extract the email from the JWT token
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // Find the user by email to get their userId
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Create the pet
            Pet savedPet = petService.createPet(user.getUserID(), petRequest);

            // Return the pet ID and a success message
            Map<String, String> response = new HashMap<>();
            response.put("petId", savedPet.getPetId());
            response.put("message", "Pet created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while creating the pet: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PostMapping("/{petId}/photos")
    public ResponseEntity<?> addPhoto(@PathVariable String petId, @RequestParam("file") MultipartFile file) {
        try {
            // Extract the email from the JWT token
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // Find the user by email to get their userId
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Verify that the pet belongs to the user
            petService.getPetsByUserId(user.getUserID()).stream()
                    .filter(p -> p.getPetId().equals(petId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Pet not found or does not belong to the user"));

            // Validate the file
            validateFile(file);

            // Add the photo
            Photo savedPhoto = petService.addPhoto(petId, file);

            // Return the photo ID and a success message
            Map<String, String> response = new HashMap<>();
            response.put("photoId", savedPhoto.getPhotoId());
            response.put("url", savedPhoto.getUrl());
            response.put("message", "Photo added successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while adding the photo: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    // File validation method
    private void validateFile(MultipartFile file) {
        // Check if the file is empty
        if (file == null || file.isEmpty()) {
            logger.error("File upload failed: File is empty");
            throw new IllegalArgumentException("File is empty. Please upload a valid file.");
        }

        // Validate file type (JPEG or PNG)
        String contentType = file.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            logger.error("File upload failed: Invalid file type - {}", contentType);
            throw new IllegalArgumentException("Invalid file type. Only JPEG and PNG files are allowed.");
        }

        // Validate file size (e.g., max 5MB)
        long maxFileSize = 5 * 1024 * 1024; // 5MB in bytes
        if (file.getSize() > maxFileSize) {
            logger.error("File upload failed: File size exceeds 5MB - {} bytes", file.getSize());
            throw new IllegalArgumentException("File size exceeds the limit of 5MB.");
        }

        logger.info("File validation passed: type={}, size={} bytes", contentType, file.getSize());
    }

    // Helper method to check if the file is a JPEG or PNG
    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }

    @GetMapping("/my-pets")
    public ResponseEntity<?> getMyPets() {
        try {
            // Extract the email from the JWT token
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // Find the user by email to get their userId
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Retrieve the user's pets
            List<Pet> pets = petService.getPetsByUserId(user.getUserID());
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while retrieving pets: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @GetMapping("/{petId}/photos")
    public ResponseEntity<?> getPetPhotos(@PathVariable String petId) {
        try {
            // Extract the email from the JWT token
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // Find the user by email to get their userId
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Verify that the pet belongs to the user
            petService.getPetsByUserId(user.getUserID()).stream()
                    .filter(p -> p.getPetId().equals(petId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Pet not found or does not belong to the user"));

            // Retrieve the pet's photos
            List<Photo> photos = petService.getPhotosByPetId(petId);
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while retrieving photos: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/update/{petId}")
    public ResponseEntity<?> updatePet(@PathVariable String petId, @Valid @RequestBody UpdatePetRequest request) {
        try {
            // Validate that the name is provided and not empty
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Pet name is required");
                return ResponseEntity.status(400).body(errorResponse);
            }

            // Get the authenticated user's email from the JWT token
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Update the pet
            Pet updatedPet = petService.updatePet(petId, user.getUserID(), request);

            // Prepare the response
            Map<String, String> response = new HashMap<>();
            response.put("petId", updatedPet.getPetId());
            response.put("message", "Pet updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while updating the pet: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{petId}")
    public ResponseEntity<?> deletePet(@PathVariable String petId) {
        try {
            // Get the authenticated user's email from the JWT token
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Delete the pet
            petService.deletePet(petId, user.getUserID());

            // Prepare the response
            Map<String, String> response = new HashMap<>();
            response.put("message", "Pet deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while deleting the pet: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
}