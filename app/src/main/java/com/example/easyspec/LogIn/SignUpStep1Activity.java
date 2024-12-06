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

public class SignUpStep1Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private Spinner spinnerUniversity;
    private Button buttonNext;
    private String selectedUniversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step1);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerUniversity = findViewById(R.id.spinnerUniversity);
        buttonNext = findViewById(R.id.buttonNext);

        // 스피너 설정
        setUpSpinners();

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

        if (email.isEmpty() || password.isEmpty() || selectedUniversity == null || selectedUniversity.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase에 유저 생성 및 첫 번째 회원가입 단계 데이터 저장
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 다음 화면으로 이동하면서 데이터 전달
                        Intent intent = new Intent(SignUpStep1Activity.this, SignUpStep2Activity.class);
                        intent.putExtra("university", selectedUniversity);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpStep1Activity.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
