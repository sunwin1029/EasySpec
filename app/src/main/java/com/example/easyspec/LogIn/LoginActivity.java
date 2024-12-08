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

// 로그인 액티비티: 사용자 이메일과 비밀번호를 사용하여 Firebase Authentication을 통한 로그인 기능을 제공
public class LoginActivity extends AppCompatActivity {
    // Firebase Authentication 인스턴스
    private FirebaseAuth mAuth;
    // 사용자 입력 필드 및 버튼 UI 요소
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 레이아웃 설정

        // Firebase Authentication 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        // 레이아웃에서 UI 요소 연결
        editTextEmail = findViewById(R.id.editTextEmail); // 이메일 입력 필드
        editTextPassword = findViewById(R.id.editTextPassword); // 비밀번호 입력 필드
        buttonLogin = findViewById(R.id.buttonLogin); // 로그인 버튼
        buttonSignup = findViewById(R.id.buttonSignup); // 회원가입 버튼

        // 로그인 버튼 클릭 시 loginUser 메서드 호출
        buttonLogin.setOnClickListener(v -> loginUser());

        // 회원가입 버튼 클릭 시 회원가입 화면으로 이동
        buttonSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpStep1Activity.class);
            startActivity(intent); // SignUpStep1Activity로 이동
        });
    }

    // 사용자가 입력한 이메일과 비밀번호로 로그인 처리
    private void loginUser() {
        // 입력된 이메일과 비밀번호 가져오기
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // 입력값이 비어 있는지 확인
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            return; // 입력값이 비어있다면 로그인 진행 중단
        }

        // Firebase Authentication을 사용하여 로그인 시도
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // 로그인 성공 시
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        // MainActivity로 이동 (로그인 후 메인 화면)
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish(); // 현재 액티비티 종료
                    } else {
                        // 로그인 실패 시 실패 메시지 표시
                        Toast.makeText(LoginActivity.this, "로그인 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
