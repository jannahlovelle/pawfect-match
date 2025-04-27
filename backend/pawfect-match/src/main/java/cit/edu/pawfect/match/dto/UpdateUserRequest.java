package cit.edu.pawfect.match.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class UpdateUserRequest {
    @NotBlank(message = "First name is required")
    private String firstName; // Added
    @NotBlank(message = "Last name is required")
    private String lastName;  // Added
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one number.")
    private String password;
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email must be valid")
    private String email;
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phone;
    private String address;
    private String profilePicture;
}