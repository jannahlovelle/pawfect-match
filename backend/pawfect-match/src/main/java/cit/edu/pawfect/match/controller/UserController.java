package cit.edu.pawfect.match.controller;

import cit.edu.pawfect.match.dto.UpdateUserRequest;
import cit.edu.pawfect.match.entity.Photo;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.service.AuthService;
import cit.edu.pawfect.match.service.PetService;
import cit.edu.pawfect.match.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import cit.edu.pawfect.match.dto.UserProfile;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    PetService petService;

    @PutMapping("/update/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        // Extract the email from the JWT token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Delegate to the service to update the user
        User updatedUser = userService.updateUser(userId, email, request);

        // Return a success response
        Map<String, String> response = new HashMap<>();
        response.put("userId", updatedUser.getUserID());
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/me")
    public ResponseEntity<Map<String, String>> deleteCurrentUser() {
        // Extract the email from the JWT token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Delegate to the service to delete the user
        userService.deleteCurrentUser(email);

        // Return a success response
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> findByEmail(@PathVariable String email) {
        try {
            User user = userService.findByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }
    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile() {
        try {
            // Extract the email from the JWT token
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // Get the user's complete profile
            UserProfile userProfile = userService.getUserProfile(email);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while retrieving user profile: " + e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

   @PutMapping("/me/pets/photos/{photoId}")
    public ResponseEntity<Map<String, String>> updatePetPhoto(
            @PathVariable String photoId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate the file
            validateFile(file);

            // Get the authenticated user
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByEmail(email);

            // Update the photo using the service
            Photo updatedPhoto = petService.updatePetPhoto(photoId, user.getEmail(), file);

            // Prepare the response
            Map<String, String> response = new HashMap<>();
            response.put("photoId", updatedPhoto.getPhotoId());
            response.put("url", updatedPhoto.getUrl());
            response.put("message", "Pet photo updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while updating the pet photo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/me/pets/photos/{photoId}")
    public ResponseEntity<Map<String, String>> deletePetPhoto(
            @PathVariable String photoId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByEmail(email);
            petService.deletePetPhoto(photoId, user.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("photoId", photoId);
            response.put("message", "Pet photo deleted successfully");
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

    @PutMapping("/{userId}/profile-picture")
    public ResponseEntity<User> updateProfilePicture(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {
        String authenticatedUserId = (String) request.getAttribute("userId");
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(403).build();
        }
        User updatedUser = authService.updateProfilePicture(userId, file);
        return ResponseEntity.ok(updatedUser);
    }
}