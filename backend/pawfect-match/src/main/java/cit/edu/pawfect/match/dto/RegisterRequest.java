package cit.edu.pawfect.match.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName; // Added
    private String lastName;  // Added
    private String password;
    private String email;
    private String phone;
    private String address;
    private String role; // Assuming this is a string in the request, will be converted to UserType
    private String profilePicture;

   
}