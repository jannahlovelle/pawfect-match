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

    public String register(RegisterRequest userRequest) {
        System.out.println("Registering user: " + userRequest.getEmail());
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

        userRepository.save(user);
        System.out.println("Saved user with email: " + user.getEmail());

        System.out.println("User registered successfully, generating token for email: " + user.getEmail());
        String token = jwtUtil.generateToken(user.getEmail());
        System.out.println("Token generated: " + token);
        System.out.println("Registration successful, returning token: " + token);
        return token;
    }

    public String login(AuthRequest authRequest) {
        System.out.println("Logging in user: " + authRequest.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        String token = jwtUtil.generateToken(authRequest.getEmail());
        System.out.println("Login successful, token generated: " + token);

        // Update last login
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + authRequest.getEmail()));
        user.setLastLogin(new Date());
        userRepository.save(user);

        return token;
    }
}