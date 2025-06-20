package com.example.digi_apps.newshive; //  PACKAGE NAME

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Import Handler for delaying

public class SplashActivity extends AppCompatActivity {

    // Duration of the splash screen in milliseconds (e.g., 3000ms = 3 seconds)
    private static int SPLASH_SCREEN_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Link this Java activity to XML layout file
        setContentView(R.layout.activity_splash);

        // Use a Handler to delay the transition to the next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start the next activity (LoginActivity).

                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i); // Start the new activity
                finish(); // Close the SplashActivity so the user can't go back to it
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}

// Splash Activity created