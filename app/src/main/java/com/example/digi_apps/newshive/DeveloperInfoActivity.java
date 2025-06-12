package com.example.digi_apps.newshive; // Make sure this matches your actual package name

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import Toolbar
import android.os.Bundle;
import android.view.View; // Import View
import android.widget.ImageButton; // Import ImageButton

public class DeveloperInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);


        // Set up the Back Button
        ImageButton backButton = findViewById(R.id.back_button_developer_info);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Go back to the previous activity (HomeActivity)
                }
            });
        }


    }
}