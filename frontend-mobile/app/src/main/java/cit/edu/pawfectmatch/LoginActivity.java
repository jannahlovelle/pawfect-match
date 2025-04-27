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
import com.google.gson.GsonBuilder;

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

    private FrameLayout loginButtonWrapper, signupButtonWrapper;
    private TextView loginButtonText, signupButtonText;
    private ProgressBar loginButtonSpinner, signupButtonSpinner;
    public static final String BASE_URL = "http://192.168.1.5:8080/";
    private ApiService apiService;

    private GoogleSignInClient gsc;

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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
                errtxt.setText("Please enter email and password");
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
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

    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToHomeActivity();
            } catch (ApiException e) {
                errtxt.setVisibility(TextView.VISIBLE);
                errtxt.setText("ERROR: " + e.getStatusCode() + " " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
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
//                        Log.e("LoginActivity", "UserID: "+userID);
//                        Log.e("LoginActivity", "Token: "+token);

                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        navigateToHomeActivity();
                    } else {
                        errtxt.setVisibility(View.VISIBLE);
                        errtxt.setText("Login failed: No token received");
                    }
                } else {
                    errtxt.setVisibility(View.VISIBLE);
                    if (response.code() == 401){
                        errtxt.setText("Invalid Login Credentials");
                    } else {
                        errtxt.setText("Login failed: " + response.code());
                    }
//                    Toast.makeText(LoginActivity.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                loginButtonSpinner.setVisibility(View.GONE);
                loginButtonText.setVisibility(View.VISIBLE);
                loginButtonText.setText("Login");
                loginButtonWrapper.setEnabled(true);

                errtxt.setVisibility(View.VISIBLE);
                errtxt.setText("ERROR: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Login", "Failure: " + t.getMessage());
            }
        });
    }
}
