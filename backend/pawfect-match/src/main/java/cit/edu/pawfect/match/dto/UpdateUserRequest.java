package cit.edu.pawfect.match.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName; // Added
    private String lastName;  // Added
    private String password;
    private String email;
    private String phone;
    private String address;
    private String profilePicture;
}