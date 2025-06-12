package com.example.digi_apps.newshive;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull; // Import for @NonNull

public class ProfileSettingsActivity extends AppCompatActivity {

    private TextView profileUsername, profileEmail, profilePassword;
    private Button editProfileButton, signOutButton;
    private ImageButton backButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers; // Reference to your "users" node

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users"); // Get reference to "users" node

        // Initialize UI elements
        profileUsername = findViewById(R.id.profile_username);
        profileEmail = findViewById(R.id.profile_email);
        profilePassword = findViewById(R.id.profile_password); // Will display obscured text
        editProfileButton = findViewById(R.id.edit_profile_button);
        signOutButton = findViewById(R.id.sign_out_button_profile);
        backButton = findViewById(R.id.back_button_profile_settings);

        // Set OnClickListener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity (HomeActivity)
            }
        });


        String email = getIntent().getStringExtra("email");

        if (email != null && !email.isEmpty()) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            usersRef.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    User user = userSnapshot.getValue(User.class);
                                    if (user != null) {
                                        // Set text views or inputs
                                        profileUsername.setText(user.username);
                                        profileEmail.setText(user.email);
                                        profilePassword.setText(user.password); // Optional
                                    }
                                }
                            } else {
                                Toast.makeText(ProfileSettingsActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ProfileSettingsActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
        }



        // Set OnClickListener for Edit Profile (placeholder for now)
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileSettingsActivity.this, "Edit Profile clicked!", Toast.LENGTH_SHORT).show();
                // TODO: Implement navigation to an Edit Profile screen later
            }
        });

        // Set OnClickListener for Sign Out
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutUser();
            }
        });


    }



    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(ProfileSettingsActivity.this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
        // Redirect to LoginActivity after sign out
        Intent intent = new Intent(ProfileSettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish(); // Finish ProfileSettingsActivity
    }
}