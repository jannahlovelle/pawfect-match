package cit.edu.pawfect.match.controller;

import cit.edu.pawfect.match.dto.AuthRequest;
import cit.edu.pawfect.match.dto.RegisterRequest;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestPart("user") RegisterRequest userRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            User savedUser = authService.register(userRequest, file);
            Map<String, String> response = new HashMap<>();
            response.put("userId", savedUser.getUserID());
            response.put("message", "User registered successfully. Please log in to obtain a token.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to upload profile picture: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) {
        try {
            Map<String, String> response = authService.login(authRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred during login: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/firebase-login")
    public ResponseEntity<Map<String, String>> firebaseLogin(
            @RequestPart("idToken") String idToken,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            Map<String, String> response = authService.firebaseLogin(idToken, file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred during Firebase login: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Server is running!");
    }
}