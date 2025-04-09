package cit.edu.pawfect.match.controller;

import cit.edu.pawfect.match.dto.UpdatePetRequest;
import cit.edu.pawfect.match.dto.UpdatePhotoRequest;
import cit.edu.pawfect.match.dto.UpdateUserRequest;
import cit.edu.pawfect.match.dto.UserProfile;
import cit.edu.pawfect.match.entity.Pet;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.service.PetService;
import cit.edu.pawfect.match.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

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
        @RequestBody UpdatePhotoRequest request) {
    petService.adminUpdatePetPhoto(photoId, request.getUrl());
    Map<String, String> response = new HashMap<>();
    response.put("photoId", photoId);
    response.put("message", "Pet photo updated successfully");
    return ResponseEntity.ok(response);
}

    @DeleteMapping("/pets/photos/{photoId}")
    public ResponseEntity<Map<String, String>> deletePetPhoto(
            @PathVariable String photoId) {
        petService.adminDeletePetPhoto(photoId);
        Map<String, String> response = new HashMap<>();
        response.put("photoId", photoId);
        response.put("message", "Pet photo deleted successfully");
        return ResponseEntity.ok(response);
    }
}