package com.example.digi_apps.newshive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo; // For keyboard action
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query; // Import Query for Firebase searches
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput; // This refers to the EditText in activity_search.xml
    private ImageButton backButton, searchButton;
    private RecyclerView searchResultsRecyclerView;
    private NewsAdapter newsAdapter; // Reuse NewsAdapter for search results
    private List<NewsArticle> searchResultsList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search); // Make sure you have activity_search.xml

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("news"); // Directly reference the "news" node

        // Initialize UI elements from activity_search.xml
        searchInput = findViewById(R.id.search_input_toolbar); // Assumes ID in activity_search.xml is search_input_toolbar
        searchButton = findViewById(R.id.search_button_toolbar);
        backButton = findViewById(R.id.back_button_search);
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);

        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity (HomeActivity)
            }
        });

        // Initialize search results list and adapter
        searchResultsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, searchResultsList); // Reuse NewsAdapter
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(newsAdapter);

        // Set up search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(searchInput.getText().toString().trim());
            }
        });

        // Set up keyboard search action (e.g., pressing "Enter" on keyboard)
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchInput.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String queryText) {
        if (TextUtils.isEmpty(queryText)) {
            Toast.makeText(this, "Please enter a search query.", Toast.LENGTH_SHORT).show();
            searchResultsList.clear();
            newsAdapter.setNewsList(searchResultsList); // Clear existing results
            return;
        }

        // Firebase query for titles starting with the queryText (case-sensitive)
        // For broader search (title, description, category), we fetch and then filter client-side.
        Query searchQuery = mDatabase.orderByChild("title") // Start with title as the primary ordered field
                .startAt(queryText)
                .endAt(queryText + "\uf8ff");

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResultsList.clear(); // Clear previous results
                if (dataSnapshot.exists()) {
                    String lowerCaseQuery = queryText.toLowerCase();
                    for (DataSnapshot articleSnapshot : dataSnapshot.getChildren()) {
                        NewsArticle newsArticle = articleSnapshot.getValue(NewsArticle.class);
                        if (newsArticle != null) {
                            // Client-side filtering for broader, case-insensitive search across multiple fields
                            String title = newsArticle.getTitle() != null ? newsArticle.getTitle().toLowerCase() : "";
                            String description = newsArticle.getDescription() != null ? newsArticle.getDescription().toLowerCase() : "";
                            String shortDescription = newsArticle.getShortDescription() != null ? newsArticle.getShortDescription().toLowerCase() : "";
                            String category = newsArticle.getCategory() != null ? newsArticle.getCategory().toLowerCase() : "";

                            if (title.contains(lowerCaseQuery) ||
                                    description.contains(lowerCaseQuery) ||
                                    shortDescription.contains(lowerCaseQuery) ||
                                    category.contains(lowerCaseQuery)) {
                                searchResultsList.add(newsArticle);
                            }
                        }
                    }
                }

                if (searchResultsList.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "No results found for '" + queryText + "'", Toast.LENGTH_SHORT).show();
                }
                newsAdapter.setNewsList(searchResultsList); // Update RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, "Search failed: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}