package com.example.digi_apps.newshive;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button verifyBtn;
    private TextView signUpInstead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        emailEditText = findViewById(R.id.email_edit_text);
        verifyBtn = findViewById(R.id.Verify_Btn);
        signUpInstead = findViewById(R.id.sign_up_instead_text);

        // Send password reset email
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email) || !email.contains("@")) {
                    emailEditText.setError("Please enter a valid email address.");
                    return;
                }

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to your email.", Toast.LENGTH_LONG).show();
                            } else {
                                String error = task.getException() != null ? task.getException().getMessage() : "Something went wrong.";
                                Toast.makeText(ForgotPasswordActivity.this, "Failed: " + error, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // Navigate to SignupActivity
        signUpInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
