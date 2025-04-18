package cit.edu.pawfectmatch.network;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // Login endpoint (returns a plain string token)
    @POST("auth/login")
    Call<LoginResponse> login(@Body AuthRequest request);
    // Register endpoint

    @POST("auth/register")
    Call<Map<String, String>> register(@Body RegisterRequest request);
    // Get user by email from MongoDB
    @GET("users/email/{email}")
    Call<UserResponse> getUserByEmail(@Path("email") String email);

}

// Response model for user data (used by getUserByEmail)
