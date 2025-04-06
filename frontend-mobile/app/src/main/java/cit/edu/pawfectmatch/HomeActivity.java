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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import cit.edu.pawfectmatch.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class HomeActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView name, email;
    Button signOutBtn;

    // Retrofit setup
    private static final String BASE_URL = "http://192.168.1.100:8080/";
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

        // Google Sign-In setup
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Show Google Sign-In data
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            name.setText(personName);
            email.setText(personEmail);
        }

        // Fetch and show MongoDB user data
        fetchUserDataFromMongoDB();

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    // Fetch user data from MongoDB via backend
    private void fetchUserDataFromMongoDB() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String jwtToken = prefs.getString("jwt_token", null);

        if (jwtToken == null) {
            Toast.makeText(this, "No JWT token found, please log in again", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UserResponse> call = apiService.getUser("Bearer " + jwtToken);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    // Combine Google and MongoDB data (or choose one)
                    name.setText("Google: " + name.getText() + " | MongoDB: " + user.getFirstName() + " " + user.getLastName());
                    email.setText("Google: " + email.getText() + " | MongoDB: " + user.getEmail());
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to fetch user data: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("HomeActivity", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("HomeActivity", "Failure: " + t.getMessage());
            }
        });
    }

    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Clear JWT token on sign out
                getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
    }

    // Response model for user data
    public static class UserResponse {
        private String firstName;
        private String lastName;
        private String email;

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
    }
}