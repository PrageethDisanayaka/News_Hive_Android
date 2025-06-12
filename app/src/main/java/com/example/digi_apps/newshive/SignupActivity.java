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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener; // To check if user exists
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


public class SignupActivity extends AppCompatActivity {

    // Declare UI elements
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;

    // Declare Firebase Database reference
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Gets root reference

        // Initialize UI elements
        usernameEditText = findViewById(R.id.signup_username_edit_text);
        emailEditText = findViewById(R.id.signup_email_edit_text);
        passwordEditText = findViewById(R.id.signup_password_edit_text);
        confirmPasswordEditText = findViewById(R.id.signup_confirm_password_edit_text);
        signUpButton = findViewById(R.id.sign_up_button);

        // Set OnClickListener for the "Sign in" link (already done, but keeping it here for context)
        TextView signInLinkText = findViewById(R.id.sign_in_link_text);
        signInLinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set OnClickListener for the "Sign up" button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        //  Client-side Validation
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required.");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return;
        }
        //  Email validation check for "@" symbol
        if (!email.contains("@")) {
            emailEditText.setError("Please enter a valid email address.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match.");
            return;
        }

        //  Check if user (email or username) already exists in Realtime Database
        // We'll store users under a "users" node, indexed by a unique key
        DatabaseReference usersRef = mDatabase.child("users");

        // Check if email already exists
        usersRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(SignupActivity.this, "Email already registered.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Email is unique, now check if username already exists
                            usersRef.orderByChild("username").equalTo(username)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Toast.makeText(SignupActivity.this, "Username already taken.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Both email and username are unique,
                                                String userId = usersRef.push().getKey(); // Generate a unique key for the user


                                                User newUser = new User(username, email, password);

                                                if (userId != null) {
                                                    usersRef.child(userId).setValue(newUser)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                                // Navigate to HomeActivity after successful registration
                                                                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                                                intent.putExtra("username", username); // pass the username
                                                                intent.putExtra("email", email);       // pass the email
                                                                startActivity(intent);
                                                                finish();

                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(SignupActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                            });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(SignupActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(SignupActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }



}