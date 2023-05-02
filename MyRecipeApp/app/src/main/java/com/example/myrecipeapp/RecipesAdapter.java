package com.example.myrecipeapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myrecipeapp.R;

import com.example.myrecipeapp.Recipe;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrecipeapp.R;
import com.example.myrecipeapp.Recipe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.net.Uri;



import java.util.ArrayList;
import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> implements Filterable {
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnEditClickListener {
        void onEditClick(Recipe recipe);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Recipe recipe);
    }

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    private List<Recipe> recipes;
    private List<Recipe> recipesFiltered;

    public RecipesAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
        this.recipesFiltered = new ArrayList<>(recipes);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipesFiltered.get(position);
        holder.titleTextView.setText(recipe.getTitle());
        holder.descriptionTextView.setText(recipe.getDescription());
        holder.categoryTextView.setText(recipe.getCategory());
        File imageFile = new File(recipe.getImageUri());
        holder.recipeImageView.setImageURI(Uri.fromFile(imageFile));

    }

    @Override
    public int getItemCount() {
        return recipesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchText = constraint.toString().toLowerCase().trim();

                if (searchText.isEmpty()) {
                    recipesFiltered = new ArrayList<>(recipes);
                } else {
                    List<Recipe> filteredList = new ArrayList<>();
                    for (Recipe recipe : recipes) {
                        if (recipe.getTitle().toLowerCase().contains(searchText)
                                || recipe.getDescription().toLowerCase().contains(searchText)
                                || recipe.getCategory().toLowerCase().contains(searchText)) {
                            filteredList.add(recipe);
                        }
                    }
                    recipesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = recipesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                recipesFiltered = (List<Recipe>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, descriptionTextView, categoryTextView;
        ImageView recipeImageView;
        Button editButton, deleteButton;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onEditClickListener.onEditClick(recipesFiltered.get(position));
                    }
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onDeleteClickListener.onDeleteClick(recipesFiltered.get(position));
                    }
                }
            });
        }

        public void bind(Recipe recipe) {
            titleTextView.setText(recipe.getTitle());
            descriptionTextView.setText(recipe.getDescription());
            categoryTextView.setText(recipe.getCategory());

            File imageFile = new File(recipe.getImageUri());
            recipeImageView.setImageURI(Uri.fromFile(imageFile));
        }
    }


}
