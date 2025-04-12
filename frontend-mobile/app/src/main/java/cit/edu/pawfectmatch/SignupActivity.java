package cit.edu.pawfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.network.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://192.168.1.5:8080/";
    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneEditText,
            addressEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize UI elements
        firstNameEditText = findViewById(R.id.signup_firstname);
        lastNameEditText = findViewById(R.id.signup_lastname);
        emailEditText = findViewById(R.id.signup_email);
        phoneEditText = findViewById(R.id.signup_phone);
        addressEditText = findViewById(R.id.signup_address);
        passwordEditText = findViewById(R.id.signup_password);
        confirmPasswordEditText = findViewById(R.id.signup_confirmPassword);
        signUpButton = findViewById(R.id.signupButton);

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Handle signup button click
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Log inputs for debugging
                Log.d("Signup", "Inputs: firstName=" + firstName + ", lastName=" + lastName +
                        ", email=" + email + ", phone=" + phone + ", address=" + address +
                        ", password=" + password);

                // Validate inputs
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                        phone.isEmpty() || address.isEmpty() || password.isEmpty() ||
                        confirmPassword.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate backend requirements
                if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
                    Toast.makeText(SignupActivity.this,
                            "Password needs 8+ chars, 1 upper, 1 lower, 1 number",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!phone.matches("^\\+?[0-9]{10,15}$")) {
                    Toast.makeText(SignupActivity.this,
                            "Invalid phone number (10-15 digits)",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send registration request
                registerUser(firstName, lastName, email, phone, address, password);
            }
        });
    }

    private void registerUser(String firstName, String lastName, String email, String phone,
                              String address, String password) {
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, phone,
                address, password, "USER", null);
        Log.d("Signup", "JSON: " + new com.google.gson.Gson().toJson(request));

        Call<Map<String, String>> call = apiService.register(request);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Map<String, String> result = response.body();
                    if (result != null && result.containsKey("userId")) {
                        Log.d("Signup", "User ID: " + result.get("userId") +
                                ", Message: " + result.get("message"));
                        Toast.makeText(SignupActivity.this,
                                result.get("message"),
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this,
                                "Registration failed: No user ID",
                                Toast.LENGTH_SHORT).show();
                        Log.e("Signup", "No userId in response");
                    }
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody().string();
                    } catch (Exception e) {
                        Log.e("Signup", "Error reading errorBody: " + e.getMessage());
                    }
                    Toast.makeText(SignupActivity.this,
                            "Registration failed: " + response.code() + " " + errorBody,
                            Toast.LENGTH_LONG).show();
                    Log.e("Signup", "Error code: " + response.code() + ", Error: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(SignupActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("Signup", "Failure: " + t.getMessage());
            }
        });
    }
}