/*package com.example.digi_apps.newshive;

public class NewsArticle {
    private String title;
    private String description;
    private String shortDescription;
    private String category;
    private String imageUrl;
    private String author;
    private String date; // Using String for date for simplicity

    // Default constructor required for Firebase
    public NewsArticle() {
    }

    // Constructor with all fields
    public NewsArticle(String title, String description, String shortDescription,
                       String category, String imageUrl, String author, String date) {
        this.title = title;
        this.description = description;
        this.shortDescription = shortDescription;
        this.category = category;
        this.imageUrl = imageUrl;
        this.author = author;
        this.date = date;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    // Setters (optional, but good for Firebase if you modify objects)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }
} */

package com.example.digi_apps.newshive; // Make sure this matches your actual package name

import java.io.Serializable;


public class NewsArticle implements Serializable { // Implement Serializable

    private String author;
    private String category;
    private String date;
    private String description; // Full description
    private String imageUrl;
    private String shortDescription;
    private String title;

    // Default constructor required for Firebase
    public NewsArticle() {
    }

    public NewsArticle(String author, String category, String date, String description,
                       String imageUrl, String shortDescription, String title) {
        this.author = author;
        this.category = category;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
        this.shortDescription = shortDescription;
        this.title = title;
    }

    // --- Getters ---
    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getTitle() {
        return title;
    }

    // --- Setters (optional, but good practice for Firebase if you might write data) ---
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}