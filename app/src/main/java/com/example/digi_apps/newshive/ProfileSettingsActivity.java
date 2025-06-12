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

import androidx.appcompat.app.AlertDialog; // Add this import
import android.content.DialogInterface; // Add this import
import com.google.firebase.auth.FirebaseAuth; // Add this import for Firebase Auth
import android.content.Intent;

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



        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettingsActivity.this, EditProfileActivity.class);
                intent.putExtra("email", profileEmail.getText().toString()); // Pass current user email
                startActivity(intent);
            }
        });


        // Initialize the Sign Out button
        signOutButton = findViewById(R.id.sign_out_button_profile);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignOutConfirmationDialog(); // Call the dialog when button is clicked
            }
        });

        // Set OnClickListener for Sign Out
        //signOutButton.setOnClickListener(new View.OnClickListener() {



    }



    private void showSignOutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out") // Title of the dialog
                .setMessage("Do You Really need to Sign out?") // Message as per your design
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Yes", so proceed with sign out
                        performSignOut();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "No", just dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert) // Optional: Add an alert icon
                .show();
    }

    private void performSignOut() {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase

        // Navigate back to the LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears activity stack
        startActivity(intent);
        finish(); // Finish the current activity
    }
}