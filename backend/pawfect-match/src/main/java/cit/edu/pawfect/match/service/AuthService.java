package cit.edu.pawfect.match.service;

import cit.edu.pawfect.match.dto.AuthRequest;
import cit.edu.pawfect.match.dto.RegisterRequest;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.entity.UserType;
import cit.edu.pawfect.match.repository.UserRepository;
import cit.edu.pawfect.match.util.JwtUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User register(RegisterRequest userRequest) {
        logger.info("Registering user: {}", userRequest.getEmail());

        // Check if the email already exists
        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
        if (existingUser.isPresent()) {
            logger.error("Email is already registered: {}", userRequest.getEmail());
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setAddress(userRequest.getAddress());
        user.setRole(userRequest.getRole() != null ? UserType.valueOf(userRequest.getRole()) : UserType.USER);
        user.setProfilePicture(userRequest.getProfilePicture());
        user.setJoinDate(new Date());
        user.setLastLogin(new Date());
        user.setSignUpMethod(userRequest.getSignUpMethod() != null ? userRequest.getSignUpMethod() : "EMAIL");

        User savedUser = userRepository.save(user);
        logger.info("Saved user with email: {}", savedUser.getEmail());
        logger.info("User registered successfully with ID: {}", savedUser.getUserID());
        return savedUser;
    }

    public Map<String, String> login(AuthRequest authRequest) {
        logger.info("Logging in user: {}", authRequest.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        // Find the user to get their userId
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", authRequest.getEmail());
                    return new RuntimeException("User not found with email: " + authRequest.getEmail());
                });

        String token = jwtUtil.generateToken(authRequest.getEmail());
        logger.info("Login successful, token generated: {}", token);

        // Update last login
        user.setLastLogin(new Date());
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("userId", user.getUserID());
        response.put("token", token);
        response.put("firstName", user.getFirstName());
        return response;
    }

    public Map<String, String> firebaseLogin(String idToken) {
        logger.info("Processing Firebase login with ID token");
    
        if (idToken == null || idToken.isEmpty()) {
            logger.error("ID token is required");
            throw new IllegalArgumentException("ID token is required");
        }
    
        try {
            // Verify the Firebase ID token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String uid = decodedToken.getUid();
            String picture = decodedToken.getPicture();
    
            // Check if the user exists in the database
            User user;
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                user = existingUser.get();
                logger.info("Existing user found with email: {}", email);
            } else {
                // User not found, create a new user
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setEmail(email);
                registerRequest.setFirstName(name != null ? name.split(" ")[0] : "Unknown");
                registerRequest.setLastName(name != null && name.split(" ").length > 1 ? name.split(" ")[1] : "User");
                registerRequest.setPassword("firebase-" + uid);
                registerRequest.setRole("USER");
                registerRequest.setSignUpMethod("GOOGLE");
                registerRequest.setProfilePicture(picture);
                user = register(registerRequest);
                logger.info("New user created with email: {}", email);
            }
    
            // Generate a JWT token for the user
            String token = jwtUtil.generateToken(user.getEmail());
            logger.info("Firebase login successful, token generated: {}", token);
    
            // Update last login
            user.setLastLogin(new Date());
            userRepository.save(user);
    
            Map<String, String> response = new HashMap<>();
            response.put("userId", user.getUserID());
            response.put("token", token);
            response.put("firstName", user.getFirstName());  // Include the firstName here
            
            return response;
        } catch (FirebaseAuthException e) {
            logger.error("Invalid Firebase ID token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid Firebase ID token: " + e.getMessage());
        }
    }  
}