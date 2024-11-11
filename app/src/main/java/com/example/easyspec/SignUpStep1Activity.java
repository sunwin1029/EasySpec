package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpStep1Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword, editTextUniversity;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step1);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUniversity = findViewById(R.id.editTextUniversity);
        buttonNext = findViewById(R.id.buttonNext);

        buttonNext.setOnClickListener(v -> goToNextStep());
    }

    private void goToNextStep() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String university = editTextUniversity.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || university.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase에 유저 생성 및 첫 번째 회원가입 단계 데이터 저장
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 다음 화면으로 이동하면서 데이터 전달
                        Intent intent = new Intent(SignUpStep1Activity.this, SignUpStep2Activity.class);
                        intent.putExtra("university", university);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpStep1Activity.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
