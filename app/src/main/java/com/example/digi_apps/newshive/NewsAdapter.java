package com.example.digi_apps.newshive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso; // Import Picasso

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<NewsArticle> newsList;

    public NewsAdapter(Context context, List<NewsArticle> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    // Method to update the data in the adapter
    public void setNewsList(List<NewsArticle> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged(); // Notify the RecyclerView that the data has changed
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_article, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsArticle newsArticle = newsList.get(position);

        holder.titleTextView.setText(newsArticle.getTitle());
        holder.shortDescriptionTextView.setText(newsArticle.getShortDescription());
        holder.categoryTextView.setText(newsArticle.getCategory());

        // Load image using Picasso
        if (newsArticle.getImageUrl() != null && !newsArticle.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(newsArticle.getImageUrl())
                    .placeholder(R.drawable.placeholder_image) // Optional: a placeholder image while loading
                    .error(R.drawable.error_image) // Optional: an error image if loading fails
                    .into(holder.imageView);
        } else {
            // If no image URL, set a default image or hide the ImageView
            holder.imageView.setImageResource(R.drawable.no_image_available); // Replace with your default
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    // ViewHolder class
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView shortDescriptionTextView;
        TextView categoryTextView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.news_image_banner);
            titleTextView = itemView.findViewById(R.id.news_article_title);
            shortDescriptionTextView = itemView.findViewById(R.id.news_short_description);
            categoryTextView = itemView.findViewById(R.id.news_category_tag);
        }
    }
}