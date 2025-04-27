package cit.edu.pawfectmatch.backendstuff;

import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("userId")
    private String userId;
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
    @SerializedName("profilePicture")
    private String profilePicture; // This will be a URL or filename

@SerializedName("password")
private String password;
    // Add other fields if needed

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
