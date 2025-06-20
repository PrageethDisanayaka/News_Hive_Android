package com.example.digi_apps.newshive;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;


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

public class EditProfileActivity extends AppCompatActivity {

    private EditText editUsername, editEmail, editPassword, editConfirmPassword;
    private Button cancelButton, saveButton;
    private DatabaseReference usersRef;
    private String userKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Get UI references
        editUsername = findViewById(R.id.edit_username);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        cancelButton = findViewById(R.id.edit_cancel_button);
        saveButton = findViewById(R.id.edit_save_button);

        String currentEmail = getIntent().getStringExtra("email");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Step 1: Load existing user data
        usersRef.orderByChild("email").equalTo(currentEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            userKey = userSnapshot.getKey(); // Save the user's key to update later
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

        // Cancel button
        cancelButton.setOnClickListener(v -> finish());

        // Save button
        saveButton.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String newPassword = editPassword.getText().toString().trim();
            String confirmPassword = editConfirmPassword.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userKey != null) {
                User updatedUser = new User(newUsername, newEmail, newPassword);
                usersRef.child(userKey).setValue(updatedUser)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "User details updated. Log in with new details.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
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
