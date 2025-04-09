package cit.edu.pawfect.match.service;

import cit.edu.pawfect.match.dto.AuthRequest;
import cit.edu.pawfect.match.dto.RegisterRequest;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.entity.UserType;
import cit.edu.pawfect.match.repository.UserRepository;
import cit.edu.pawfect.match.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User register(RegisterRequest userRequest) {
        System.out.println("Registering user: " + userRequest.getEmail());

        // Check if the email already exists
        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setAddress(userRequest.getAddress());
        user.setRole(UserType.valueOf(userRequest.getRole()));
        user.setProfilePicture(userRequest.getProfilePicture());
        user.setJoinDate(new Date());
        user.setLastLogin(new Date());

        User savedUser = userRepository.save(user);
        System.out.println("Saved user with email: " + savedUser.getEmail());

        System.out.println("User registered successfully with ID: " + savedUser.getUserID());
        return savedUser; // Return the saved user
    }

    public Map<String, String> login(AuthRequest authRequest) {
        System.out.println("Logging in user: " + authRequest.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        // Find the user to get their userId
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + authRequest.getEmail()));

        String token = jwtUtil.generateToken(authRequest.getEmail());
        System.out.println("Login successful, token generated: " + token);

        // Update last login
        user.setLastLogin(new Date());
        userRepository.save(user);

        // Return the userId (ObjectId) and token in a Map
        Map<String, String> response = new HashMap<>();
        response.put("userId", user.getUserID());
        response.put("token", token);
        return response;
    }
}