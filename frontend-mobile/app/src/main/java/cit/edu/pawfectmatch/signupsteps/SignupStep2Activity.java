package cit.edu.pawfectmatch.signupsteps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import cit.edu.pawfectmatch.LoginActivity;
import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.network.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupStep2Activity extends AppCompatActivity {

    private EditText passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private ImageView profileImageView;
    private TextView selectProfilePic;
    private ApiService apiService;

    private String base64ProfilePicture = null;

    // Image Picker Launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        profileImageView.setImageBitmap(bitmap);
                        base64ProfilePicture = encodeImageToBase64(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step2);

        passwordEditText = findViewById(R.id.signup_password);
        confirmPasswordEditText = findViewById(R.id.signup_confirmPassword);
        signUpButton = findViewById(R.id.signupButton);
        profileImageView = findViewById(R.id.profileImageView);
        selectProfilePic = findViewById(R.id.selectProfilePic);

        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        // Show password
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_off_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    } else {
                        // Hide password
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    }
                    // Move cursor to the end
                    passwordEditText.setSelection(passwordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        confirmPasswordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (confirmPasswordEditText.getRight() - confirmPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (confirmPasswordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        // Show password
                        confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_off_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    } else {
                        // Hide password
                        confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    }
                    // Move cursor to the end
                    confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LoginActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Image selection
        selectProfilePic.setOnClickListener(v -> openImagePicker());

        // Signup
        signUpButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill both password fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = getIntent();
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");
            String email = intent.getStringExtra("email");
            String phone = intent.getStringExtra("phone");
            String address = intent.getStringExtra("address");

            registerUser(firstName, lastName, email, phone, address, password, base64ProfilePicture);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    private void registerUser(String firstName, String lastName, String email, String phone,
                              String address, String password, String profilePicture) {

        RegisterRequest request = new RegisterRequest(firstName, lastName, email, phone,
                address, password, "USER", profilePicture);

        Log.d("Signup", "JSON: " + new com.google.gson.Gson().toJson(request));

        Call<Map<String, String>> call = apiService.register(request);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Map<String, String> result = response.body();
                    if (result != null && result.containsKey("userId")) {
                        Log.d("Signup", "User ID: " + result.get("userId"));
                        Toast.makeText(SignupStep2Activity.this,
                                result.get("message"), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignupStep2Activity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignupStep2Activity.this,
                                "Registration failed: No user ID",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody().string();
                    } catch (Exception e) {
                        Log.e("Signup", "Error reading errorBody: " + e.getMessage());
                    }
                    Toast.makeText(SignupStep2Activity.this,
                            "Registration failed: " + response.code() + " " + errorBody,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(SignupStep2Activity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
