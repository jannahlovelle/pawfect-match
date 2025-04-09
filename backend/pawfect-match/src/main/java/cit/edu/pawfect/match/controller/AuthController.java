package cit.edu.pawfect.match.controller;

import cit.edu.pawfect.match.dto.AuthRequest;
import cit.edu.pawfect.match.dto.RegisterRequest;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.service.AuthService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid@RequestBody RegisterRequest userRequest) {
        try {
            User savedUser = authService.register(userRequest);
            Map<String, String> response = new HashMap<>();
            response.put("userId", savedUser.getUserID());
            response.put("message", "User registered successfully. Please log in to obtain a token.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            System.out.println("Registration failed with exception: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred during registration: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest authRequest) {
        try {
            Map<String, String> response = authService.login(authRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Login failed with exception: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred during login: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}