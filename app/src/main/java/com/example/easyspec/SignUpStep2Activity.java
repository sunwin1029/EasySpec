package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpStep2Activity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Spinner spinnerLaptop, spinnerTablet, spinnerPhone;
    private Button buttonFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step2);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        spinnerLaptop = findViewById(R.id.spinnerLaptop);
        spinnerTablet = findViewById(R.id.spinnerTablet);
        spinnerPhone = findViewById(R.id.spinnerPhone);
        buttonFinish = findViewById(R.id.buttonFinish);

        setUpSpinners();

        buttonFinish.setOnClickListener(v -> saveUserInfo());
    }

    private void setUpSpinners() {
        String[] laptops = {"Dell", "HP", "Apple", "Lenovo"};
        String[] tablets = {"iPad", "Samsung Galaxy Tab", "Surface"};
        String[] phones = {"iPhone", "Samsung Galaxy", "Pixel"};

        ArrayAdapter<String> laptopAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, laptops);
        spinnerLaptop.setAdapter(laptopAdapter);

        ArrayAdapter<String> tabletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tablets);
        spinnerTablet.setAdapter(tabletAdapter);

        ArrayAdapter<String> phoneAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, phones);
        spinnerPhone.setAdapter(phoneAdapter);
    }

    private void saveUserInfo() {
        String university = getIntent().getStringExtra("university");
        String laptop = spinnerLaptop.getSelectedItem().toString();
        String tablet = spinnerTablet.getSelectedItem().toString();
        String phone = spinnerPhone.getSelectedItem().toString();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Users user = new Users(FirebaseAuth.getInstance().getCurrentUser().getEmail(), university, laptop, tablet, phone);

        mDatabase.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpStep2Activity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                // 회원가입 완료 후 로그인 화면으로 돌아가기
                Intent intent = new Intent(SignUpStep2Activity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // 기존 액티비티 스택을 모두 제거
                startActivity(intent);
                finish();  // 현재 액티비티 종료
            } else {
                Toast.makeText(SignUpStep2Activity.this, "회원정보 저장 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
