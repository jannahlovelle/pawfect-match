package cit.edu.pawfectmatch.network;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("userID")
    private String userID;

    @SerializedName("token")
    private String token;

    public String getUserID() {
        return userID;
    }
    public String getToken() {
        return token;
    }
}