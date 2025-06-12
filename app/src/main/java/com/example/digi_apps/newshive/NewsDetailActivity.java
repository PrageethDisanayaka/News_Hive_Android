package com.example.digi_apps.newshive;

public class NewsDetailActivity {
}
/*
package com.example.digi_apps.newshive; // Make sure this matches your actual package name

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso; // Import Picasso

public class NewsDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NEWS_ARTICLE = "extra_news_article"; // Key for Intent extra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_news_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide default title
        }

        // Set up the Back Button
        ImageButton backButton = findViewById(R.id.back_button_news_detail);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Go back to the previous activity
                }
            });
        }

        // Get references to your UI elements
        ImageView newsImage = findViewById(R.id.news_detail_image);
        TextView newsTitle = findViewById(R.id.news_detail_title);
        TextView newsAuthor = findViewById(R.id.news_detail_author);
        TextView newsDate = findViewById(R.id.news_detail_date);
        TextView newsDescription = findViewById(R.id.news_detail_description);

        // Get the NewsArticle object from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NEWS_ARTICLE)) {
            NewsArticle article = (NewsArticle) intent.getSerializableExtra(EXTRA_NEWS_ARTICLE);

            if (article != null) {
                // Populate UI with news details
                newsTitle.setText(article.getTitle());
                newsAuthor.setText(article.getAuthor());
                newsDate.setText(article.getDate());
                newsDescription.setText(article.getDescription());

                // Load image using Picasso
                if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                    Picasso.get()
                            .load(article.getImageUrl())
                            .placeholder(R.drawable.no_image_available) // Optional: a placeholder drawable
                            .error(R.drawable.error_image)         // Optional: an error drawable
                            .into(newsImage);
                } else {
                    // If no image URL, set a default placeholder or hide ImageView
                    newsImage.setImageResource(R.drawable.no_image_available);
                }
            }
        } else {
            // Handle case where no article data is passed (e.g., show error, finish activity)
            newsTitle.setText("Error loading news details");
            // You might want to finish() the activity here or show a Toast
        }
    }
}*/