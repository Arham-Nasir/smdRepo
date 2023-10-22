package com.example.assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.pass);
        TextView signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User creation successful
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // Store additional user data in Firestore
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", email);
                                    userData.put("password", password);

                                    mFirestore.collection("users")
                                            .document(user.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Document added successfully
                                                    Log.d("myTag", "Document added with ID: " + user.getUid());

                                                    // Navigate to registration activity
                                                    Intent intent = new Intent(MainActivity.this, registration.class);
                                                    startActivity(intent);
                                                    finish(); // Optional: This will close the current activity
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle Firestore data insertion failure
                                                    Log.e("myTag", "Error adding document", e);
                                                    Toast.makeText(MainActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // User creation failed
                                    Log.e("myTag", "User creation failed: " + task.getException().getMessage());
                                    Toast.makeText(MainActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        TextView forgotPasswordTextView= findViewById(R.id.forgotPasswordTextView);

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, forget_password.class);
                startActivity(intent);

            }
        });

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WelcomeScreen.class);
                startActivity(intent);

            }
        });    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(MainActivity.this, user.getUid(), Toast.LENGTH_SHORT).show();
        }
    }
}
