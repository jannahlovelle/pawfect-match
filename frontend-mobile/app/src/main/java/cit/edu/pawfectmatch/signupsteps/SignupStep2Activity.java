package cit.edu.pawfectmatch.signupsteps;

import static cit.edu.pawfectmatch.LoginActivity.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import cit.edu.pawfectmatch.LoginActivity;
import cit.edu.pawfectmatch.R;
import cit.edu.pawfectmatch.network.ApiService;
import cit.edu.pawfectmatch.network.RegisterRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.OkHttpClient;

public class SignupStep2Activity extends AppCompatActivity {

    private EditText passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private ImageView profileImageView;
    private TextView selectProfilePic;
    private TextView errorTxt;
    private ApiService apiService;
    private Uri profileImageUri = null;

    // Image Picker Launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    profileImageUri = result.getData().getData();
                    try {
                        profileImageView.setImageURI(profileImageUri);
                        String mimeType = getContentResolver().getType(profileImageUri);
                        Log.d("Signup", "Selected image URI: " + profileImageUri + ", MIME type: " + mimeType);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        Log.e("Signup", "Image load error: " + e.getMessage());
                        errorTxt.setText("Failed to load image");
                    }
                }
            });

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step2);

        passwordEditText = findViewById(R.id.signup_password);
        confirmPasswordEditText = findViewById(R.id.signup_confirmPassword);
        signUpButton = findViewById(R.id.signupButton);
        profileImageView = findViewById(R.id.profileImageView);
        selectProfilePic = findViewById(R.id.selectProfilePic);
        errorTxt = findViewById(R.id.signup2_error);

        // Initialize Retrofit with logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        apiService = retrofit.create(ApiService.class);
        Log.d("Signup", "BASE_URL: " + BASE_URL);

        // Password visibility toggle for password
        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_off_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    } else {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    }
                    passwordEditText.setSelection(passwordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        // Password visibility toggle for confirm password
        confirmPasswordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (confirmPasswordEditText.getRight() - confirmPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (confirmPasswordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_off_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    } else {
                        confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    }
                    confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        // Image selection
        selectProfilePic.setOnClickListener(v -> openImagePicker());
        profileImageView.setOnClickListener(v -> openImagePicker());

        // Signup
        signUpButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                errorTxt.setText("Please fill both password fields");
                Toast.makeText(this, "Please fill both password fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                errorTxt.setText("Passwords do not match");
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = getIntent();
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");
            String email = intent.getStringExtra("email");
            String phone = intent.getStringExtra("phone");
            String address = intent.getStringExtra("address");

            registerUser(firstName, lastName, email, phone, address, password);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Allow all image types
        try {
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open image picker", Toast.LENGTH_SHORT).show();
            Log.e("Signup", "Image picker error: " + e.getMessage());
            errorTxt.setText("Unable to open image picker");
        }
    }

    private File createFileFromUri(Uri uri) {
        try {
            String mimeType = getContentResolver().getType(uri);
            Log.d("Signup", "Original MIME type: " + mimeType);

            // Use .jpg extension to match ProfileViewModel, but preserve original bytes
            File file = new File(getCacheDir(), "profile_image_" + System.currentTimeMillis() + ".jpg");
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            Log.d("Signup", "Created file: " + file.getAbsolutePath() + ", size: " + file.length() + " bytes, MIME type: " + mimeType);
            return file;
        } catch (Exception e) {
            Log.e("Signup", "Error creating file from Uri: " + e.getMessage());
            errorTxt.setText("Failed to process image");
            return null;
        }
    }

    private String validateFile(File file) {
        if (file == null || !file.exists()) {
            return "Image file is invalid or does not exist";
        }

        // Check file size (5MB = 5 * 1024 * 1024 bytes)
        long maxFileSize = 5 * 1024 * 1024;
        if (file.length() > maxFileSize) {
            return "Image size exceeds 5MB: " + file.length() + " bytes";
        }

        return null; // File is valid
    }

    private void registerUser(String firstName, String lastName, String email, String phone,
                              String address, String password) {
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, phone,
                address, password, "USER");

        // Convert RegisterRequest to JSON RequestBody
        Gson gson = new Gson();
        String json = gson.toJson(request);
        RequestBody userRequestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Log.d("Signup", "User JSON: " + json);

        MultipartBody.Part filePart = null;
        if (profileImageUri != null) {
            File file = createFileFromUri(profileImageUri);
            if (file != null && file.exists()) {
                String validationError = validateFile(file);
                if (validationError != null) {
                    Toast.makeText(this, validationError, Toast.LENGTH_LONG).show();
                    Log.e("Signup", "Validation error: " + validationError);
                    errorTxt.setText(validationError);
                    // Fallback: Proceed without profile picture
                    Toast.makeText(this, "Retrying registration without image...", Toast.LENGTH_SHORT).show();
                    registerWithoutProfilePicture(userRequestBody);
                    return;
                }
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                Log.d("Signup", "File part created: " + file.getName() + ", size: " + file.length() + " bytes");
            } else {
                Toast.makeText(this, "Failed to process image file", Toast.LENGTH_SHORT).show();
                Log.e("Signup", "File is null or does not exist");
                errorTxt.setText("Failed to process image file");
                // Fallback: Proceed without profile picture
                Toast.makeText(this, "Retrying registration without image...", Toast.LENGTH_SHORT).show();
                registerWithoutProfilePicture(userRequestBody);
                return;
            }
        }

        Call<Map<String, String>> call = apiService.register(userRequestBody, filePart);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Map<String, String> result = response.body();
                    if (result != null && result.containsKey("userId")) {
                        Log.d("Signup", "User ID: " + result.get("userId"));
                        Toast.makeText(SignupStep2Activity.this,
                                result.get("message") != null ? result.get("message") : "Registration successful",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignupStep2Activity.this, LoginActivity.class));
                        finish();
                    } else {
                        String errorMsg = "Registration failed: Invalid response from server";
                        Toast.makeText(SignupStep2Activity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("Signup", errorMsg + ": " + response.body());
                        errorTxt.setText(errorMsg);
                    }
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e("Signup", "Error reading errorBody: " + e.getMessage());
                    }
                    String errorMsg = "Registration failed: HTTP " + response.code() + ": " + errorBody;
                    Toast.makeText(SignupStep2Activity.this, "Failed to upload image", Toast.LENGTH_LONG).show();
                    Log.e("Signup", errorMsg);
                    errorTxt.setText("Failed to upload image");
                    // Fallback: Retry without profile picture
                    if (profileImageUri != null) {
                        Toast.makeText(SignupStep2Activity.this,
                                "Retrying registration without image...",
                                Toast.LENGTH_SHORT).show();
                        registerWithoutProfilePicture(userRequestBody);
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Toast.makeText(SignupStep2Activity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("Signup", errorMsg);
                errorTxt.setText("Network error");
                // Fallback: Retry without profile picture
                if (profileImageUri != null) {
                    Toast.makeText(SignupStep2Activity.this,
                            "Retrying registration without image...",
                            Toast.LENGTH_SHORT).show();
                    registerWithoutProfilePicture(userRequestBody);
                }
            }
        });
    }

    private void registerWithoutProfilePicture(RequestBody userRequestBody) {
        Call<Map<String, String>> call = apiService.register(userRequestBody, null);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Map<String, String> result = response.body();
                    if (result != null && result.containsKey("userId")) {
                        Log.d("Signup", "User ID: " + result.get("userId"));
                        Toast.makeText(SignupStep2Activity.this,
                                result.get("message") != null ? result.get("message") : "Registration successful",
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignupStep2Activity.this, LoginActivity.class));
                        finish();
                    } else {
                        String errorMsg = "Registration failed: Invalid response from server";
                        Toast.makeText(SignupStep2Activity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e("Signup", errorMsg + ": " + response.body());
                        errorTxt.setText(errorMsg);
                    }
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                    } catch (Exception e) {
                        Log.e("Signup", "Error reading errorBody: " + e.getMessage());
                    }
                    String errorMsg = "Registration failed: HTTP " + response.code() + ": " + errorBody;
                    Toast.makeText(SignupStep2Activity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("Signup", errorMsg);
                    errorTxt.setText(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Toast.makeText(SignupStep2Activity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e("Signup", errorMsg);
                errorTxt.setText(errorMsg);
            }
        });
    }
}