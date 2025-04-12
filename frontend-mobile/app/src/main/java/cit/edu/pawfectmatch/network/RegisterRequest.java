package cit.edu.pawfectmatch.network;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private String address;
    @SerializedName("password")
    private String password;
    @SerializedName("role")
    private String role;
    @SerializedName("profilePicture")
    private String profilePicture;

    public RegisterRequest(String firstName, String lastName, String email, String phone,
                           String address, String password, String role, String profilePicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.role = role;
        this.profilePicture = profilePicture;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getProfilePicture() { return profilePicture; }
}
