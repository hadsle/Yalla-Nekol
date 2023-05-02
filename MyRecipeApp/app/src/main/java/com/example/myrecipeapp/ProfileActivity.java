package com.example.myrecipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvEmail;
    private EditText etNewPassword, etConfirmNewPassword;
    private Button btnChangePassword;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        tvEmail = findViewById(R.id.tvEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        int userId = databaseHelper.getUserId();
        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                updateUserInfo(user);
            }

            btnChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newPassword = etNewPassword.getText().toString();
                    String confirmNewPassword = etConfirmNewPassword.getText().toString();

                    if (newPassword.equals(confirmNewPassword)) {
                        if (databaseHelper.updateUserPassword(userId, newPassword)) {
                            Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = new DatabaseHelper(ProfileActivity.this);
                databaseHelper.signOut();

                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_add:
                        intent = new Intent(ProfileActivity.this, AddRecipeActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_profile:
                        intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_show:
                        intent = new Intent(ProfileActivity.this, ShowRecipesActivity.class);
                        startActivity(intent);
                        break;

                }
                return false;
            }
        });

    }

    private void updateUserInfo(User user) {
        TextView emailTextView = findViewById(R.id.tvEmail);
        emailTextView.setText(user.getEmail());
    }

}