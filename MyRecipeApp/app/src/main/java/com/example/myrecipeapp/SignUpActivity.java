package com.example.myrecipeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    EditText emailSignUpEditText, passwordSignUpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        databaseHelper = new DatabaseHelper(this);
        emailSignUpEditText = findViewById(R.id.emailSignUpEditText);
        passwordSignUpEditText = findViewById(R.id.passwordSignUpEditText);

        Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailSignUpEditText.getText().toString();
                String password = passwordSignUpEditText.getText().toString();

                if (databaseHelper.registerUser(email, password)) {
                    Toast.makeText(SignUpActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Error creating account. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
