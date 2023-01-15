package com.example.smsgroups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViewById(R.id.btnSignUp).setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        findViewById(R.id.btnLogIn).setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}