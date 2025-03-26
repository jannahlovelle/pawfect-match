package cit.edu.pawfect.match.service;

import cit.edu.pawfect.match.dto.RegisterRequest;
import cit.edu.pawfect.match.dto.UpdateUserRequest;
import cit.edu.pawfect.match.entity.User;
import cit.edu.pawfect.match.entity.UserType;
import cit.edu.pawfect.match.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    // Setter injection for PasswordEncoder
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest registerRequest) {
        // Validate the email
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already taken");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setAddress(registerRequest.getAddress());
        user.setRole(registerRequest.getRole() != null ? UserType.valueOf(registerRequest.getRole()) : UserType.USER);
        user.setProfilePicture(registerRequest.getProfilePicture());
        user.setJoinDate(new Date());
        user.setLastLogin(new Date());

        User savedUser = userRepository.save(user);
        System.out.println("Saved user with userId: " + savedUser.getUserID() + ", email: " + savedUser.getEmail());
        return savedUser;
    }

    public User updateUser(String userId, UpdateUserRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));

        // Check if the email is being updated and ensure it's unique
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            if (!updateRequest.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new IllegalArgumentException("Email is already taken");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().isEmpty()) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null && !updateRequest.getLastName().isEmpty()) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            // Validate the new password
            if (updateRequest.getPassword().length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters long");
            }
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            System.out.println("Password updated for user with userId: " + userId);
        }
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }
        if (updateRequest.getAddress() != null) {
            user.setAddress(updateRequest.getAddress());
        }
        if (updateRequest.getProfilePicture() != null) {
            user.setProfilePicture(updateRequest.getProfilePicture());
        }

        return userRepository.save(user);
    }

    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with userId: " + userId));
        userRepository.delete(user);
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return user;
    }
}