package com.example.easyspec.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.MainActivity;
import com.example.easyspec.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);  // 회원가입 버튼

        // 로그인 버튼 클릭 시 로그인 기능 수행
        buttonLogin.setOnClickListener(v -> loginUser());

        // 회원가입 버튼 클릭 시 회원가입 화면으로 이동
        buttonSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpStep1Activity.class);
            startActivity(intent);  // 회원가입 화면으로 이동
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));  // 로그인 후 프로필 화면으로 이동
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
