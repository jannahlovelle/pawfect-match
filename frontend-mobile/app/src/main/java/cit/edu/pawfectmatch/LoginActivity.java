package cit.edu.pawfectmatch;

import java.util.logging.Handler;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private int failedAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_TIME = 15000;
    private EditText email, password;
    private TextView signupText;
    private Button loginButton;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupLinkText);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = email.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("user") && pass.equals("1234")) {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    failedAttempts = 0; // Reset failed attempts on success
                } else {
                    failedAttempts++;
                    Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();

                    if (failedAttempts >= MAX_ATTEMPTS) {
                        loginButton.setEnabled(false);
                        Toast.makeText(LoginActivity.this, "Too many failed attempts! Try again in 15 seconds.", Toast.LENGTH_LONG).show();

                        // Re-enable the button after 15 seconds
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loginButton.setEnabled(true);
                                failedAttempts = 0; // Reset failed attempts after cooldown
                            }
                        }, LOCK_TIME);

                    }
                }
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }
}
