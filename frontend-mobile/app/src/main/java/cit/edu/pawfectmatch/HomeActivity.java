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
import cit.edu.pawfectmatch.network.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
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

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct!=null){
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

    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
    }

//  MONGODB FUNCTIONS
private void fetchUserDataFromMongoDB() {
    SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
    String userEmail = prefs.getString("user_email", null);

    if (userEmail == null) {
        Toast.makeText(this, "No email found, please log in again", Toast.LENGTH_SHORT).show();
        return;
    }

    Call<UserResponse> call = apiService.getUserByEmail(userEmail);
    call.enqueue(new Callback<UserResponse>() {
        @Override
        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                UserResponse user = response.body();
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

//    void signOut() {
//        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
//                finish();
//                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//            }
//        });
//    }
}