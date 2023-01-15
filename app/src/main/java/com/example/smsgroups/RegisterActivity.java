package com.example.smsgroups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private Button btnLogIn, btnSignIn;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailInput = findViewById(R.id.editTextSignEmail);
        passwordInput = findViewById(R.id.editTextSignPsw);

        btnLogIn = (Button) findViewById(R.id.btnSgnSignIn);
        btnSignIn = (Button) findViewById(R.id.btnSgnSignUp);

        mAuth = FirebaseAuth.getInstance();

        btnLogIn.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        btnSignIn.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurunuz", Toast.LENGTH_SHORT).show();
                return;
            }

            if(password.length() < 8) {
                Toast.makeText(this, "Şifreniz en az 8 karakterden fazla olmalıdır", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(this, "Kayıt başarılı", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Kayıt başarısız", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}