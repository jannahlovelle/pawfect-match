package cit.edu.pawfect.match.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

import java.util.Date;

import org.springframework.data.annotation.Id;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String userId;

    private String firstName; // Added
    private String lastName;  // Added
    private String password; 
    private String email;
    private String phone;
    private String address;

    @Field("userType")
    private UserType role; 
    
    private String profilePicture;
    private Date joinDate;
    private Date lastLogin;
    private String signUpMethod; 

    public User() {
    }

    public User(String firstName, String lastName, String password, String email, String phone, String address, UserType role, String profilePicture, Date joinDate, Date lastLogin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.profilePicture = profilePicture;
        this.joinDate = joinDate;
        this.lastLogin = lastLogin;
    }

    public String getUserID() {
        return userId;
    }
    public void setUserID(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public UserType getRole() {
        return role;
    }
    public void setRole(UserType role) {
        this.role = role;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Date getJoinDate() {
        return joinDate;
    }
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Date getLastLogin() {
        return lastLogin;
    }
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}