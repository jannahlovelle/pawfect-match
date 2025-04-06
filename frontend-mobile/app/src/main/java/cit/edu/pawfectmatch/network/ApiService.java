package cit.edu.pawfectmatch.network;

import cit.edu.pawfectmatch.HomeActivity;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<String> login(@Body AuthRequest request);
    @GET("auth/user")
    Call<HomeActivity.UserResponse> getUser(@Header("Authorization") String token);
}