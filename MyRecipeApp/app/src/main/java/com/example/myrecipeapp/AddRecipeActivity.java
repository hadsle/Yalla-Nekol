package com.example.myrecipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import android.view.MenuItem;

import android.graphics.Bitmap;

import java.io.FileOutputStream;

public class AddRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    DatabaseHelper databaseHelper;
    EditText recipeTitleEditText, recipeDescriptionEditText, recipeCategoryEditText;
    Button saveRecipeButton, chooseImageButton;
    ImageView recipeImageView;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        databaseHelper = new DatabaseHelper(this);
        recipeTitleEditText = findViewById(R.id.recipeTitleEditText);
        recipeDescriptionEditText = findViewById(R.id.recipeDescriptionEditText);
        recipeCategoryEditText = findViewById(R.id.recipeCategoryEditText);
        recipeImageView = findViewById(R.id.recipeImageView);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        saveRecipeButton = findViewById(R.id.saveRecipeButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        saveRecipeButton.setOnClickListener(v -> {
            String title = recipeTitleEditText.getText().toString();
            String description = recipeDescriptionEditText.getText().toString();
            String category = recipeCategoryEditText.getText().toString();

            if (imageUri != null && !title.isEmpty() && !description.isEmpty() && !category.isEmpty()) {
                try {
                    // Generate a unique file name using a timestamp
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String imageFileName = "RECIPE_" + timeStamp;

                    File imageFile = new File(getCacheDir(), imageFileName);
                    saveBitmapToFile(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri), imageFile);
                    if (databaseHelper.addRecipe(title, description, category, imageFile.getPath())) {
                        Toast.makeText(AddRecipeActivity.this, "Recipe added successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddRecipeActivity.this, "Error adding recipe. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(AddRecipeActivity.this, "Error saving image. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddRecipeActivity.this, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_add:
                        intent = new Intent(AddRecipeActivity.this, AddRecipeActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_profile:
                        intent = new Intent(AddRecipeActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_show:
                        intent = new Intent(AddRecipeActivity.this, ShowRecipesActivity.class);
                        startActivity(intent);
                        break;

                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            recipeImageView.setImageURI(imageUri);
        }


    }



    public void saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
