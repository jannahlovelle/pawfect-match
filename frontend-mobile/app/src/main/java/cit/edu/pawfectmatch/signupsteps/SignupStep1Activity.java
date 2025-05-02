package cit.edu.pawfectmatch.signupsteps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import cit.edu.pawfectmatch.R;

public class SignupStep1Activity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneEditText, addressEditText;
    private FrameLayout nextButtonWrapper;
    private TextView nextStepText;
    private ProgressBar nextStepSpinner;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step1);

        // Initialize form fields
        firstNameEditText = findViewById(R.id.signup_first_name);
        lastNameEditText = findViewById(R.id.signup_last_name);
        emailEditText = findViewById(R.id.signup_email);
        phoneEditText = findViewById(R.id.signup_phone);
        addressEditText = findViewById(R.id.signup_address);

        // Initialize button wrapper and animation views
        nextButtonWrapper = findViewById(R.id.next_step_button_wrapper);
        nextStepText = findViewById(R.id.next_step_button_text);
        nextStepSpinner = findViewById(R.id.next_step_button_spinner);

        // Handle "Next" click
        nextButtonWrapper.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();

            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(SignupStep1Activity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading spinner
            nextStepText.setVisibility(View.INVISIBLE);
            nextStepSpinner.setVisibility(View.VISIBLE);

            // Proceed to next step
            Intent intent = new Intent(SignupStep1Activity.this, SignupStep2Activity.class);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("address", address);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset button on return
        nextStepText.setVisibility(View.VISIBLE);
        nextStepSpinner.setVisibility(View.GONE);
    }
}
