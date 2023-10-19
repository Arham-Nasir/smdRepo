package com.arhamnasir.i191962;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView signUpTextView;
    EditText emailEditTextLogin;
    EditText passwordEditTextLogin;
    Button loginButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        signUpTextView = findViewById(R.id.signUpTextView);
        emailEditTextLogin = findViewById(R.id.emailEditTextLogin);
        passwordEditTextLogin = findViewById(R.id.passwordEditTextLogin);
        loginButton = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, registration.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditTextLogin.getText().toString();
                String password = passwordEditTextLogin.getText().toString();

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null ) {
                                        // User's email is verified, allow them to log in
                                        Toast.makeText(MainActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, bottomnav.class);
                                        startActivity(intent);
                                    } else {
                                        // User's email is not verified, show a message and prevent login
                                        Toast.makeText(MainActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Login failed, show an error message
                                    Toast.makeText(MainActivity.this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FirebaseAuth", "Login failed: " + e.getMessage());
                            }
                        });
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        mAuth.signOut();
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, bottomnav.class);
            startActivity(intent);

        }
    }

}
