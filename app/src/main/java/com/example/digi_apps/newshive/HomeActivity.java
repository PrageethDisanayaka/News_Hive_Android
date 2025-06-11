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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        newsRecyclerView = findViewById(R.id.news_recycler_view);
        searchEditText = findViewById(R.id.search_edit_text);

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

        // Initialize newsList and NewsAdapter
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsList); // Pass context and empty list

        // Set up RecyclerView
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setAdapter(newsAdapter);

        // Fetch news data from Firebase
        fetchNewsData();
    }

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