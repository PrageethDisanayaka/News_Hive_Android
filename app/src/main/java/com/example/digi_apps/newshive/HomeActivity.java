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



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List

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
        overflowMenuButton = findViewById(R.id.overflow_menu_button);



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

    @Override
    protected void onStart() {
        super.onStart();
        // --- NEW: Call updateWelcomeMessage() here ---
        updateWelcomeMessage();
        // --- END NEW ---
    }






    //  this method  for update welcome msg
    private void updateWelcomeMessage() {
        // Get the currently signed-in Firebase user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Try to get the user's display name first
            String userName = currentUser.getDisplayName();

            if (userName != null && !userName.isEmpty()) {
                // If display name exists, use it
                homeTitleText.setText("Welcome back, " + userName + "!");
            } else {
                // If display name is not set, try to use part of their email
                String email = currentUser.getEmail();
                if (email != null && !email.isEmpty()) {
                    String namePart = email.split("@")[0]; // Get the part before '@'
                    homeTitleText.setText("Welcome back, " + namePart + "!");
                } else {
                    // Fallback to a generic message if no name or email part is available
                    homeTitleText.setText("Welcome back, User!");
                }
            }
        } else {
            // Fallback if no user is currently logged in (shouldn't happen if login is mandatory)
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
                    Toast.makeText(HomeActivity.this, "Profile settings clicked!", Toast.LENGTH_SHORT).show();
                    // TODO: Implement navigation to Profile Settings Activity
                    return true;
                } else if (itemId == R.id.menu_developer_info) {
                    Toast.makeText(HomeActivity.this, "Developer info clicked!", Toast.LENGTH_SHORT).show();
                    // TODO: Implement navigation to Developer Information Activity
                    return true;
                } else if (itemId == R.id.menu_sign_out) {
                    // Sign out the user
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(HomeActivity.this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
                    // Redirect to LoginActivity after sign out
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                    startActivity(intent);
                    finish(); // Finish HomeActivity
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
}