package cit.edu.pawfectmatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cit.edu.pawfectmatch.network.*;
import cit.edu.pawfectmatch.signupsteps.SignupStep1Activity;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView signupText, errtxt;
    private Button loginButton;
    private ImageView googleBtn;
    private ProgressBar loginProgress;

    private FrameLayout loginButtonWrapper, signupButtonWrapper, googleButtonWrapper;
    private TextView loginButtonText, signupButtonText, googleButtonText;
    private ProgressBar loginButtonSpinner, signupButtonSpinner, googleButtonSpinner;
    public static final String BASE_URL = "http://192.168.1.7:8080/";
    private ApiService apiService;

    private GoogleSignInClient gsc;
    private static int RC_SIGN_IN=1000;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI references
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        errtxt = findViewById(R.id.login_errorView);

        loginButtonWrapper = findViewById(R.id.login_button_wrapper);
        loginButtonText = findViewById(R.id.login_button_text);
        loginButtonSpinner = findViewById(R.id.login_button_spinner);

        signupButtonWrapper = findViewById(R.id.signup_button_wrapper);
        signupButtonText = findViewById(R.id.signup_button_text);
        signupButtonSpinner = findViewById(R.id.signup_button_spinner);

        googleButtonWrapper = findViewById(R.id.google_button_wrapper);
        googleButtonText = findViewById(R.id.google_button_text);
        googleButtonSpinner = findViewById(R.id.google_button_spinner);



        password.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        // Show password
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_off_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    } else {
                        // Hide password
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.visibility_24dp_e3e3e3_fill0_wght400_grad0_opsz24, 0);
                    }
                    // Move cursor to the end
                    password.setSelection(password.getText().length());
                    return true;
                }
            }
            return false;
        });

                // Google sign-in setup
        //        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        //                .requestEmail()
        //                .build();
        //        gsc = GoogleSignIn.getClient(this, gso);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Add this to request Firebase ID token
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

                // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        apiService = retrofit.create(ApiService.class);

        // Click listeners
        loginButtonWrapper.setOnClickListener(v -> {
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            String stremail = email.getText().toString().trim();
            String strpassword = password.getText().toString().trim();

            if (stremail.isEmpty() || strpassword.isEmpty()) {
                errtxt.setVisibility(TextView.VISIBLE);
                errtxt.setText("Please enter both email and password");
                Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(stremail, strpassword);
            }
        });

        signupButtonWrapper.setOnClickListener(v -> {
            signupButtonSpinner.setVisibility(View.VISIBLE);
            signupButtonText.setVisibility(View.INVISIBLE);
            signupButtonWrapper.setEnabled(false);

            Intent signUpIntent = new Intent(LoginActivity.this, SignupStep1Activity.class);
            startActivity(signUpIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        googleButtonWrapper.setOnClickListener(v -> {
            googleButtonSpinner.setVisibility(View.VISIBLE);
            googleButtonText.setVisibility(View.INVISIBLE);
            signupButtonWrapper.setEnabled(false);

            signIn();
        });
    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GoogleSignIn", "Google Sign-In successful, ID token: " + account.getIdToken().substring(0, 20) + "...");
//                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("GoogleSignIn", "Google Sign-In failed: " + e.getStatusCode() + " " + e.getMessage());
                errtxt.setVisibility(TextView.VISIBLE);
                errtxt.setText("Google Sign-In failed. Please try again.");
                Toast.makeText(this, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginUser(String email, String password) {
        loginButtonSpinner.setVisibility(View.VISIBLE);
        loginButtonText.setVisibility(View.INVISIBLE);
        loginButtonWrapper.setEnabled(false);

        AuthRequest authRequest = new AuthRequest(email, password);
        Call<LoginResponse> call = apiService.login(authRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginButtonSpinner.setVisibility(View.GONE);
                loginButtonText.setVisibility(View.VISIBLE);
                loginButtonText.setText("Login");
                loginButtonWrapper.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    String userID = loginResponse.getUserID();

                    if (token != null) {
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit()
                                .putString("jwt_token", token)
                                .putString("user_id", userID)
                                .putString("user_email", email)
                                .apply();

                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        navigateToHomeActivity();
                    } else {
                        errtxt.setVisibility(View.VISIBLE);
                        errtxt.setText("Login failed: No token received");
                        Toast.makeText(LoginActivity.this, "Login failed: No token received", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "An error occurred. Please try again.";
                    int statusCode = response.code();

                    // Try to parse error message from response body
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorBody);
                            errorMessage = jsonObject.optString("message", errorMessage);
                        }
                    } catch (IOException | JSONException e) {
                        Log.e("Login", "Error parsing error body: " + e.getMessage());
                    }

                    // Map status codes to user-friendly messages
                    switch (statusCode) {
                        case 400:
                            errorMessage = "Invalid request. Please check your input.";
                            break;
                        case 401:
                            errorMessage = "Invalid email or password.";
                            break;
                        case 403:
                            errorMessage = "Access denied. Please contact support.";
                            break;
                        case 500:
                            errorMessage = "Server error. Please try again later.";
                            break;
                    }

                    errtxt.setVisibility(View.VISIBLE);
                    errtxt.setText(errorMessage);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("Login", "HTTP Error: " + statusCode + " - " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginButtonSpinner.setVisibility(View.GONE);
                loginButtonText.setVisibility(View.VISIBLE);
                loginButtonText.setText("Login");
                loginButtonWrapper.setEnabled(true);

                String errorMessage;
                if (t instanceof UnknownHostException) {
                    errorMessage = "No internet connection. Please check your network.";
                } else if (t instanceof SocketTimeoutException) {
                    errorMessage = "Server timeout. Please try again.";
                } else {
                    errorMessage = "Unable to connect. Please try again.";
                }

                errtxt.setVisibility(View.VISIBLE);
                errtxt.setText(errorMessage);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("Login", "Network Failure: " + t.getMessage());
            }
        });
    }

//    UI METHODS

    @Override
    protected void onResume() {
        super.onResume();
        signupButtonSpinner.setVisibility(View.GONE);
        signupButtonText.setVisibility(View.VISIBLE);
        signupButtonWrapper.setEnabled(true);
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}