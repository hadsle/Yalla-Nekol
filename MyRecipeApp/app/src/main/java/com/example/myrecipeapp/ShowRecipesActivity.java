package com.example.myrecipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.MediaStore;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ShowRecipesActivity extends AppCompatActivity implements RecipesAdapter.OnEditClickListener, RecipesAdapter.OnDeleteClickListener {
    private RecipesAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private DatabaseHelper databaseHelper;

    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    private String updatedImagePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            updatedImagePath = getPathFromURI(selectedImageUri);

        }

    }

    public String getPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipes);
        EditText searchEditText = findViewById(R.id.searchEditText);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().toLowerCase().trim();
                recipeAdapter.getFilter().filter(searchText);
            }
        });

        databaseHelper = new DatabaseHelper(this);
        recipeList = databaseHelper.getAllRecipes();

        RecyclerView recyclerView = findViewById(R.id.recipesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipesAdapter(recipeList);
        recyclerView.setAdapter(recipeAdapter);

        recipeAdapter.setOnEditClickListener(this);
        recipeAdapter.setOnDeleteClickListener(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_add:
                        intent = new Intent(ShowRecipesActivity.this, AddRecipeActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_profile:
                        intent = new Intent(ShowRecipesActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_show:
                        intent = new Intent(ShowRecipesActivity.this, ShowRecipesActivity.class);
                        startActivity(intent);
                        break;

                }
                return false;
            }



        });

    }

    // ...

    @Override
    public void onEditClick(Recipe recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_recipe_dialog, null);
        builder.setView(dialogView);

        EditText editRecipeTitleEditText = dialogView.findViewById(R.id.editRecipeTitleEditText);
        EditText editRecipeDescriptionEditText = dialogView.findViewById(R.id.editRecipeDescriptionEditText);
        EditText editRecipeCategoryEditText = dialogView.findViewById(R.id.editRecipeCategoryEditText);
        Button updateRecipeButton = dialogView.findViewById(R.id.updateRecipeButton);
        Button editRecipeImageButton = dialogView.findViewById(R.id.editRecipeImageButton);

        editRecipeTitleEditText.setText(recipe.getTitle());
        editRecipeDescriptionEditText.setText(recipe.getDescription());
        editRecipeCategoryEditText.setText(recipe.getCategory());
        editRecipeImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        });

        AlertDialog dialog = builder.create();

        updateRecipeButton.setOnClickListener(v -> {
            String updatedTitle = editRecipeTitleEditText.getText().toString();
            String updatedDescription = editRecipeDescriptionEditText.getText().toString();
            String updatedCategory = editRecipeCategoryEditText.getText().toString();
            String updatedImagePath = recipe.getImageUri(); // Assuming the image path is not changed

            boolean isUpdated = databaseHelper.updateRecipe(recipe.getId(), updatedTitle, updatedDescription, updatedCategory, updatedImagePath);

            if (isUpdated) {
                // Refresh the activity
                recreate();
                Toast.makeText(this, "Recipe updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error updating recipe. Please try again.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }


    @Override
    public void onDeleteClick(Recipe recipe) {
        boolean isDeleted = databaseHelper.deleteRecipe(recipe.getId());

        if (isDeleted) {
            recreate();
            recipeList.remove(recipe);
            recipeAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Recipe deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error deleting recipe. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}

