package com.example.digi_apps.newshive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // For TextUtils.isEmpty()
import android.view.View;
import android.widget.Button; // Import Button
import android.widget.EditText; // Import EditText
import android.widget.TextView;
import android.widget.Toast; // Import Toast

import com.google.firebase.database.DataSnapshot; // Import DataSnapshot
import com.google.firebase.database.DatabaseError; // Import DatabaseError
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener; // Import ValueEventListener


public class LoginActivity extends AppCompatActivity {

    // Declare UI elements
    private EditText emailEditText, passwordEditText;
    private Button signInButton;

    // Declare Firebase Database reference
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        signInButton = findViewById(R.id.sign_in_button);

        // Set OnClickListener for the "Sign up Instead" text (already done)
        TextView signUpInsteadText = findViewById(R.id.sign_up_instead_text);
        signUpInsteadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the "Sign in" button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Optional: Implement Forgot Password link if needed later
        TextView forgotPasswordText = findViewById(R.id.forgot_password_text);
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Forgot Password clicked!", Toast.LENGTH_SHORT).show();
                // Later: Implement password reset functionality
            }
        });
    }

    private void loginUser() {
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        // 1. Client-side Validation
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return;
        }
        if (!email.contains("@")) { // Basic email format validation
            emailEditText.setError("Please enter a valid email address.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return;
        }

        // 2. Query Firebase Realtime Database for the user
        DatabaseReference usersRef = mDatabase.child("users");
        usersRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // User with this email exists, now verify password
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class); // Get User object


                                
                                if (user != null && user.password.equals(password)) {
                                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();


                                    // Navigate to HomeActivity
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra("username", user.username); // Pass the username
                                    intent.putExtra("email", user.email);       //  Also pass email
                                    startActivity(intent);
                                    finish();
                                    // Exit loop after finding the user
                                }
                            }
                            // If loop finishes, it means email matched but password didn't (or user was null)
                            //Toast.makeText(LoginActivity.this, "Incorrect email or password.", Toast.LENGTH_SHORT).show();

                        } else {
                            // No user found with this email
                            Toast.makeText(LoginActivity.this, "Incorrect email or password.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}