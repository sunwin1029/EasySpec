package com.example.easyspec;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.Data.Users;
import com.example.easyspec.LogIn.ChangePasswordFragment;
import com.example.easyspec.LogIn.DeviceChangeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private Spinner spinnerLaptop, spinnerTablet, spinnerPhone, spinnerUniversity;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button buttonUpdate;
    private TextView textViewUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        spinnerUniversity = findViewById(R.id.universitySpinner);
        buttonUpdate = findViewById(R.id.saveButton);
        textViewUserEmail = findViewById(R.id.newTextBelowUserId);

        Button buttonChangePassword = findViewById(R.id.changePasswordButton);
        buttonChangePassword.setOnClickListener(v -> {
            ChangePasswordFragment fragment = new ChangePasswordFragment();
            fragment.show(getSupportFragmentManager(), "ChangePasswordFragment");
        });

        Button buttonChangeDevice = findViewById(R.id.changeDeviceButton);
        buttonChangeDevice.setOnClickListener(v -> {
            // 현재 대학교 정보를 Fragment에 전달
            String university = spinnerUniversity.getSelectedItem().toString();
            DeviceChangeFragment deviceChangeFragment = DeviceChangeFragment.newInstance(university);
            deviceChangeFragment.show(getSupportFragmentManager(), "DeviceChangeFragment");
        });

        // 스피너 초기화
        setUpSpinners();

        // 데이터 불러오기
        loadUserProfile();

        // 업데이트 버튼 클릭 리스너
        buttonUpdate.setOnClickListener(v -> updateUserInfo());
    }

    private void setUpSpinners() {
        // 스피너 설정을 위한 어댑터 초기화
        ArrayAdapter<CharSequence> universityAdapter = ArrayAdapter.createFromResource(this, R.array.university_list, android.R.layout.simple_spinner_item);
        universityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUniversity.setAdapter(universityAdapter);
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

                        // 대학교, 노트북, 태블릿, 핸드폰 정보 설정
                        setSpinnerValue(spinnerUniversity, user.getUniversity());
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
        String university = spinnerUniversity.getSelectedItem().toString();
        String laptop = spinnerLaptop.getSelectedItem().toString();
        String tablet = spinnerTablet.getSelectedItem().toString();
        String phone = spinnerPhone.getSelectedItem().toString();
        String userId = mAuth.getCurrentUser().getUid();

        // Firebase에서 해당 사용자 정보 수정
        mDatabase.child(userId).child("university").setValue(university);
        mDatabase.child(userId).child("laptop").setValue(laptop);
        mDatabase.child(userId).child("tablet").setValue(tablet);
        mDatabase.child(userId).child("phone").setValue(phone).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "회원정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "회원정보 수정 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
