package cit.edu.pawfect.match.controller;

import cit.edu.pawfect.match.dto.UpdatePhotoRequest;
import cit.edu.pawfect.match.dto.UpdateUserRequest;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.service.PetService;
import cit.edu.pawfect.match.service.UserService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import cit.edu.pawfect.match.dto.UserProfile;

@RestController
@RequestMapping("/users")
public class UserController {

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
            @RequestBody UpdatePhotoRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        petService.updatePetPhoto(photoId, user.getUserID(), request.getUrl());
        Map<String, String> response = new HashMap<>();
        response.put("photoId", photoId);
        response.put("message", "Pet photo updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me/pets/photos/{photoId}")
    public ResponseEntity<Map<String, String>> deletePetPhoto(
            @PathVariable String photoId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        petService.deletePetPhoto(photoId, user.getUserID());
        Map<String, String> response = new HashMap<>();
        response.put("photoId", photoId);
        response.put("message", "Pet photo deleted successfully");
        return ResponseEntity.ok(response);
    }
}