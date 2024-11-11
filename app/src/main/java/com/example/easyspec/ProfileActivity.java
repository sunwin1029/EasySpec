package com.example.easyspec;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private Spinner spinnerLaptop, spinnerTablet, spinnerPhone;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button buttonUpdate;
    private TextView textViewUserEmail;
    private EditText editTextUniversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        spinnerLaptop = findViewById(R.id.spinnerLaptop);
        spinnerTablet = findViewById(R.id.spinnerTablet);
        spinnerPhone = findViewById(R.id.spinnerPhone);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        editTextUniversity = findViewById(R.id.editTextUniversity);

        // 스피너 초기화
        setUpSpinners();

        // 데이터 불러오기
        loadUserProfile();

        // 업데이트 버튼 클릭 리스너
        buttonUpdate.setOnClickListener(v -> updateUserInfo());
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

    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users user = dataSnapshot.getValue(Users.class);

                    if (user != null) {
                        // 이메일 정보 표시
                        textViewUserEmail.setText(user.getEmail());

                        // 대학교 정보 표시
                        editTextUniversity.setText(user.getUniversity());

                        // 스피너 값 설정
                        setSpinnerValue(spinnerLaptop, user.getLaptop());
                        setSpinnerValue(spinnerTablet, user.getTablet());
                        setSpinnerValue(spinnerPhone, user.getPhone());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "데이터 불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    private void updateUserInfo() {
        String laptop = spinnerLaptop.getSelectedItem().toString();
        String tablet = spinnerTablet.getSelectedItem().toString();
        String phone = spinnerPhone.getSelectedItem().toString();
        String university = editTextUniversity.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();

        // Firebase에서 해당 사용자 정보 수정
        mDatabase.child(userId).child("laptop").setValue(laptop);
        mDatabase.child(userId).child("tablet").setValue(tablet);
        mDatabase.child(userId).child("phone").setValue(phone);
        mDatabase.child(userId).child("university").setValue(university).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "회원정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "회원정보 수정 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

