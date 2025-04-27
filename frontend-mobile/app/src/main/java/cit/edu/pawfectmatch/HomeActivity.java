package cit.edu.pawfectmatch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.GsonBuilder;


import cit.edu.pawfectmatch.backendstuff.UserData;
import cit.edu.pawfectmatch.backendstuff.UserProfile;
import cit.edu.pawfectmatch.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    TextView name, email;
    Button signOutBtn;

    private static final String BASE_URL = "http://192.168.1.5:8080/";
    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homescreen);

        name = findViewById(R.id.home_username);
        email = findViewById(R.id.home_email);
        signOutBtn = findViewById(R.id.home_signOutBtn);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

// Retrofit setup if not globally initialized
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Replace with your actual base URL
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

// Add "Bearer " before the token
        Call<UserProfile> call = apiService.getUserProfile("Bearer " + token);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    UserData user = response.body().getUser();
                    String strfirstName = user.getFirstName();
                    String stremail = user.getEmail();

                    name.setText(strfirstName);
                    email.setText(stremail);
                } else {
                    Log.e("UserProfile", "Response error or null user object: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e("UserProfile", "Failure: " + t.getMessage());
            }
        });


    }
    }