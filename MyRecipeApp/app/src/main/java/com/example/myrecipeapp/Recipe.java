package com.example.myrecipeapp;

import android.util.Log;

public class Recipe {
    private int id;
    private String title;
    private String description;
    private String imageUrl;

    private String category;
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    private static final String KEY_IMAGE_URL = "image_url";

    public Recipe(int id, String title, String description, String category,String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Add getters and setters for the fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public String getImageUri() {
        Log.d("my info",imageUrl);
        return imageUrl;
    }


    public void setCategory(String category) {
        this.category = category;
    }
    public static final String TABLE_NAME = "recipes";

}
