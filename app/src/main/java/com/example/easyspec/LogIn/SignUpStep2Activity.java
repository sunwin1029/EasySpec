package com.example.easyspec.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.R;
import com.example.easyspec.Data.Users;
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

        ArrayAdapter<CharSequence> laptopAdapter = ArrayAdapter.createFromResource(this, R.array.laptop_options, android.R.layout.simple_spinner_item);
        laptopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLaptop.setAdapter(laptopAdapter);

        ArrayAdapter<CharSequence> tabletAdapter = ArrayAdapter.createFromResource(this, R.array.tablet_options, android.R.layout.simple_spinner_item);
        tabletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTablet.setAdapter(tabletAdapter);

        ArrayAdapter<CharSequence> phoneAdapter = ArrayAdapter.createFromResource(this, R.array.phone_options, android.R.layout.simple_spinner_item);
        phoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
