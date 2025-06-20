package com.example.digi_apps.newshive;

import androidx.annotation.NonNull; // Import for @NonNull annotation
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Import LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast; // Import Toast
import androidx.appcompat.widget.PopupMenu;
import android.view.MenuItem; // Import MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;




import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth; //  this import for Firebase Auth
import android.content.Intent; //  this import for Intent

public class HomeActivity extends AppCompatActivity {

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter; // Declare NewsAdapter
    private List<NewsArticle> newsList; // Declare list for news articles
    private DatabaseReference mDatabase;
    private EditText searchEditText;


    private TextView homeTitleText; // This is the TextView for the welcome message
    private FirebaseAuth mAuth;

    private ImageButton overflowMenuButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        // Initialize UI elements
        newsRecyclerView = findViewById(R.id.news_recycler_view);
        searchEditText = findViewById(R.id.search_edit_text);
        homeTitleText = findViewById(R.id.home_title_text);



        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Set default selected tab to "Education"
        bottomNav.setSelectedItemId(R.id.nav_education);
        filterNewsByCategory("education"); // Load education news on login

        // Handle tab selection
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_sports) {
                filterNewsByCategory("sports");
                return true;
            } else if (id == R.id.nav_education) {
                filterNewsByCategory("education");
                return true;
            } else if (id == R.id.nav_events) {
                filterNewsByCategory("events");
                return true;
            }

            return false;
        });






        // Set up Top Bar Back Button (from UI doc)
        ImageButton backButtonHome = findViewById(R.id.back_button_home);
        backButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This button navigates back. For Home screen, typically goes to previous activity
                // or closes the app if Home is the base. For now, let's finish the activity.
                finish();
            }
        });



        // Set OnClickListener for the search EditText to go to SearchActivity
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        overflowMenuButton = findViewById(R.id.overflow_menu_button);


        // --- NEW: Set OnClickListener for the overflow menu button ---
        overflowMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v); // Pass the view (button) as anchor
            }
        });
        // --- END NEW ---


        // Initialize newsList and NewsAdapter
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsList); // Pass context and empty list

        // Set up RecyclerView
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);

        // Fetch news data from Firebase
        fetchNewsData();
    }

    private void filterNewsByCategory(String category) {
        DatabaseReference newsRef = mDatabase.child("news");

        newsRef.orderByChild("category").equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        newsList.clear();
                        for (DataSnapshot articleSnapshot : snapshot.getChildren()) {
                            NewsArticle article = articleSnapshot.getValue(NewsArticle.class);
                            if (article != null) {
                                newsList.add(article);
                            }
                        }
                        newsAdapter.setNewsList(newsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeActivity.this, "Failed to load news.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // --- NEW: Call updateWelcomeMessage() here ---
        updateWelcomeMessage();
        // --- END NEW ---
    }





    //  this method  for update welcome msg
    private void updateWelcomeMessage() {
        // Get username passed from LoginActivity
        String username = getIntent().getStringExtra("username");

        if (username != null && !username.isEmpty()) {
            homeTitleText.setText("Hi.. Welcome , " + username + "!");
        } else {
            homeTitleText.setText("Welcome to NewsHive!");
        }
    }





    // --- NEW: Method to show and handle PopupMenu ---
    private void showPopupMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.menu_home_overflow, popup.getMenu()); // Inflate your menu XML

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_profile_settings) {
                    // Navigate to ProfileSettingsActivity
                    String username = getIntent().getStringExtra("username");
                    String email = getIntent().getStringExtra("email");

                    Intent intent = new Intent(HomeActivity.this, ProfileSettingsActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_developer_info) {
                    Toast.makeText(HomeActivity.this, "Developer info clicked!", Toast.LENGTH_SHORT).show();
                    // : Implement navigation to Developer Information Activity
                    Intent intent = new Intent(HomeActivity.this, DeveloperInfoActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_sign_out) {
                    showSignOutConfirmationDialog();
                    return true;
                }
                return false;
            }
        });
        popup.show(); // Show the pop-up menu
    }
    // --- END NEW ---


    private void fetchNewsData() {
        // Get a reference to the "news" node in your Firebase Realtime Database
        DatabaseReference newsRef = mDatabase.child("news");

        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newsList.clear(); // Clear old data before adding new data
                if (dataSnapshot.exists()) {
                    for (DataSnapshot articleSnapshot : dataSnapshot.getChildren()) {
                        NewsArticle newsArticle = articleSnapshot.getValue(NewsArticle.class);
                        if (newsArticle != null) {
                            newsList.add(newsArticle);
                        }
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "No news articles found in database.", Toast.LENGTH_SHORT).show();
                }
                newsAdapter.setNewsList(newsList); // Update adapter with new data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Failed to load news: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

        getSharedPreferences("loginPrefs", MODE_PRIVATE).edit().clear().apply();

    }



}