package cit.edu.pawfect.match.controller;

import cit.edu.pawfect.match.dto.UpdatePetRequest;
import cit.edu.pawfect.match.dto.UpdateUserRequest;
import cit.edu.pawfect.match.dto.UserProfile;
import cit.edu.pawfect.match.entity.Pet;
import cit.edu.pawfect.match.entity.Photo;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.service.PetService;
import cit.edu.pawfect.match.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/pets")
    public ResponseEntity<List<Pet>> getAllPets() {
        List<Pet> pets = petService.getAllPets();
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/users-with-pets")
    public ResponseEntity<List<UserProfile>> getAllUsersWithPets() {
        List<UserProfile> userProfiles = userService.getAllUserProfiles();
        return ResponseEntity.ok(userProfiles);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.adminUpdateUser(userId, request);
        Map<String, String> response = new HashMap<>();
        response.put("userId", updatedUser.getUserID());
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUser(userId, email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/pets/{petId}")
    public ResponseEntity<Map<String, String>> updatePet(
            @PathVariable String petId,
            @Valid @RequestBody UpdatePetRequest request) {
        Pet updatedPet = petService.adminUpdatePet(petId, request);
        Map<String, String> response = new HashMap<>();
        response.put("petId", updatedPet.getPetId());
        response.put("message", "Pet updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/pets/{petId}")
    public ResponseEntity<Map<String, String>> deletePet(@PathVariable String petId) {
        petService.adminDeletePet(petId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Pet deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/pets/photos/{photoId}")
    public ResponseEntity<Map<String, String>> updatePetPhoto(
            @PathVariable String photoId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate the file
            validateFile(file);

            // Update the photo using the service
            Photo updatedPhoto = petService.adminUpdatePetPhoto(photoId, file);

            // Prepare the response
            Map<String, String> response = new HashMap<>();
            response.put("photoId", updatedPhoto.getPhotoId());
            response.put("url", updatedPhoto.getUrl());
            response.put("message", "Pet photo updated successfully by admin");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while updating the pet photo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/pets/photos/{photoId}")
    public ResponseEntity<Map<String, String>> deletePetPhoto(
            @PathVariable String photoId) {
        try {
            petService.adminDeletePetPhoto(photoId);
            Map<String, String> response = new HashMap<>();
            response.put("photoId", photoId);
            response.put("message", "Pet photo deleted successfully by admin");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while deleting the pet photo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
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

        long maxFileSize = 5 * 1024 * 1024; // 5MB in bytes
        if (file.getSize() > maxFileSize) {
            logger.error("File upload failed: File size exceeds 5MB - {} bytes", file.getSize());
            throw new IllegalArgumentException("File size exceeds the limit of 5MB.");
        }

        logger.info("File validation passed: type={}, size={} bytes", contentType, file.getSize());
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }
}
// 