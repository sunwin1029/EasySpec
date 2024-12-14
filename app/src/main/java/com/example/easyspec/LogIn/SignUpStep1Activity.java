package com.example.easyspec.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.R;
import com.google.firebase.auth.FirebaseAuth;
import android.util.Log; // Log 클래스를 사용하기 위해 추가

public class SignUpStep1Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private Spinner spinnerUniversity;
    private Button buttonNext;
    private String selectedUniversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step1); // 레이아웃 설정

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail); // 이메일 입력 필드
        editTextPassword = findViewById(R.id.editTextPassword); // 비밀번호 입력 필드
        spinnerUniversity = findViewById(R.id.spinnerUniversity); // 대학 선택 스피너
        buttonNext = findViewById(R.id.buttonNext); // 다음 단계 버튼

        // 스피너 설정
        setUpSpinners();

        // 다음 단계 버튼 클릭 시 goToNextStep 메서드 호출
        buttonNext.setOnClickListener(v -> goToNextStep());
    }

    private void setUpSpinners() {
        // 대학 목록 배열 가져오기
        String[] universities = getResources().getStringArray(R.array.university_list);

        // 대학 스피너에 어댑터 설정
        ArrayAdapter<String> universityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, universities);
        universityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUniversity.setAdapter(universityAdapter);

        // 스피너에서 선택된 대학 정보 저장
        spinnerUniversity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 선택된 아이템이 null이 아니고 유효한지 확인
                if (position != AdapterView.INVALID_POSITION) {
                    selectedUniversity = parentView.getItemAtPosition(position).toString();
                    Log.d("EasySpec", "Selected university: " + selectedUniversity); // 선택된 대학 로그 추가
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무것도 선택되지 않았을 때 처리 (필요시)
                selectedUniversity = null;
            }
        });
    }

    private void goToNextStep() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // 필드 유효성 검사
        if (email.isEmpty() || password.isEmpty() || selectedUniversity == null || selectedUniversity.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            Log.d("EasySpec", "Sign up failed: Not all fields are filled."); // 로그 추가
            return;
        }

        // Firebase에 유저 생성
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 사용자 등록 성공
                        String userId = mAuth.getCurrentUser().getUid();
                        Log.d("EasySpec", "Sign up successful: User ID = " + userId); // 로그 추가
                        // 다음 화면으로 이동하면서 데이터 전달
                        Intent intent = new Intent(SignUpStep1Activity.this, SignUpStep2Activity.class);
                        intent.putExtra("userId", userId); // UID 전달
                        intent.putExtra("email", email);
                        intent.putExtra("university", selectedUniversity);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpStep1Activity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("EasySpec", "Sign up failed: " + task.getException().getMessage()); // 로그 추가
                    }
                });
    }
}
