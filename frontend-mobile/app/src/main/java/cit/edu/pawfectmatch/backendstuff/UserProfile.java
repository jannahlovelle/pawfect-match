package cit.edu.pawfectmatch.backendstuff;

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.SerializedName;

public class UserProfile {

    @SerializedName("user")
    private UserData user;

    public UserData getUser() {
        return user;
    }
}
