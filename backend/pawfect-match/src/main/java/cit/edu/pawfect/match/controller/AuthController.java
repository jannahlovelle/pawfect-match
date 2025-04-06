package cit.edu.pawfect.match.controller;

import cit.edu.pawfect.match.dto.AuthRequest;
import cit.edu.pawfect.match.dto.RegisterRequest;
import cit.edu.pawfect.match.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest userRequest) {
        try {
            String token = authService.register(userRequest);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            System.out.println("Registration failed with exception: " + e.getMessage());
            return ResponseEntity.status(500).body("An error occurred during registration: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            String token = authService.login(authRequest);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            System.out.println("Login failed with exception: " + e.getMessage());
            return ResponseEntity.status(500).body("An error occurred during login: " + e.getMessage());
        }
    }

    @GetMapping("/test")
        public ResponseEntity<String> test() {
            return ResponseEntity.ok("Server is running!");
    }
}