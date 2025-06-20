package com.example.digi_apps.newshive;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editUsername, editEmail, editPassword, editConfirmPassword;
    private Button cancelButton, saveButton;
    private DatabaseReference usersRef;
    private String userKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI elements
        editUsername = findViewById(R.id.edit_username);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        cancelButton = findViewById(R.id.edit_cancel_button);
        saveButton = findViewById(R.id.edit_save_button);

        String currentEmail = getIntent().getStringExtra("email");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Load existing user data
        usersRef.orderByChild("email").equalTo(currentEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            userKey = userSnapshot.getKey(); // Save user key for update
                            if (user != null) {
                                editUsername.setText(user.username);
                                editEmail.setText(user.email);
                                editPassword.setText(user.password);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditProfileActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Cancel button listener
        cancelButton.setOnClickListener(v -> finish());

        // Save button logic
        saveButton.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String newPassword = editPassword.getText().toString().trim();
            String confirmPassword = editConfirmPassword.getText().toString().trim();

            // Validation
            if (newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                editEmail.setError("Enter a valid email address");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                editConfirmPassword.setError("Passwords do not match.");
                return;
            }

            if (userKey != null) {
                User updatedUser = new User(newUsername, newEmail, newPassword);
                usersRef.child(userKey).setValue(updatedUser)
                        .addOnSuccessListener(aVoid -> {
                            // Clear login state
                            getSharedPreferences("loginPrefs", MODE_PRIVATE)
                                    .edit()
                                    .clear()
                                    .apply();

                            // Show dialog
                            new AlertDialog.Builder(EditProfileActivity.this)
                                    .setTitle("Details Updated")
                                    .setMessage("Your details have been updated.\nPlease log in with your new credentials.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .show();

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            } else {
                Toast.makeText(this, "User key not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
