package com.example.easyspec.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.Data.Users;
import com.example.easyspec.MainActivity;
import com.example.easyspec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private Spinner spinnerUniversity;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference productDatabase;
    private Button buttonUpdate;
    private TextView textViewUserEmail;
    private String currentUniversity;
    private TextView pointValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Firebase 인증 및 데이터베이스 참조 초기화
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        productDatabase = FirebaseDatabase.getInstance().getReference("ProductItems");

        // UI 요소 초기화
        spinnerUniversity = findViewById(R.id.universitySpinner);
        buttonUpdate = findViewById(R.id.saveButton);
        textViewUserEmail = findViewById(R.id.newTextBelowUserId);
        pointValue = findViewById(R.id.pointsValue);

        // 비밀번호 변경 버튼 클릭 리스너 설정
        Button buttonChangePassword = findViewById(R.id.changePasswordButton);
        buttonChangePassword.setOnClickListener(v -> {
            ChangePasswordFragment fragment = new ChangePasswordFragment();
            fragment.show(getSupportFragmentManager(), "ChangePasswordFragment");
        });

        // 기기 변경 버튼 클릭 리스너 설정
        Button buttonChangeDevice = findViewById(R.id.changeDeviceButton);
        buttonChangeDevice.setOnClickListener(v -> {
            String university = spinnerUniversity.getSelectedItem().toString();
            DeviceChangeFragment deviceChangeFragment = DeviceChangeFragment.newInstance(university);
            deviceChangeFragment.show(getSupportFragmentManager(), "DeviceChangeFragment");
        });

        // 스피너 설정 및 사용자 프로필 로드
        setUpSpinners();
        loadUserProfile();

        // 사용자 정보 업데이트 버튼 클릭 리스너 설정
        buttonUpdate.setOnClickListener(v -> updateUserInfo());
    }

    // 대학 스피너 설정 메서드
    private void setUpSpinners() {
        ArrayAdapter<CharSequence> universityAdapter = ArrayAdapter.createFromResource(this, R.array.university_list, android.R.layout.simple_spinner_item);
        universityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUniversity.setAdapter(universityAdapter);
    }

    // 사용자 프로필을 로드하는 메서드
    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null) {
                        textViewUserEmail.setText(user.getEmail());
                        currentUniversity = user.getUniversity();
                        setSpinnerValue(spinnerUniversity, currentUniversity);
                        pointValue.setText(Integer.toString(user.getPoint()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "데이터 불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 스피너의 값을 설정하는 메서드
    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    // 사용자 정보를 업데이트하는 메서드
    private void updateUserInfo() {
        String university = spinnerUniversity.getSelectedItem().toString();
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child(userId).child("university").setValue(university).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!currentUniversity.equals(university)) {
                    updateUserNum(userId, currentUniversity, university);
                }
                Toast.makeText(ProfileActivity.this, "회원정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(ProfileActivity.this, "회원정보 수정 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // 사용자 수를 업데이트하는 메서드
    private void updateUserNum(String userId, String oldUniversity, String newUniversity) {
        Log.d("EasySpec", "Updating user ID: " + userId + ", Old University: " + oldUniversity + ", New University: " + newUniversity);

        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String laptop = dataSnapshot.child("laptop").getValue(String.class);
                    String tablet = dataSnapshot.child("tablet").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);

                    updateDeviceUserNum(oldUniversity, newUniversity, laptop);
                    updateDeviceUserNum(oldUniversity, newUniversity, tablet);
                    updateDeviceUserNum(oldUniversity, newUniversity, phone);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "사용자 기기 정보 업데이트 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 기기 사용자 수를 업데이트하는 메서드
    private void updateDeviceUserNum(String oldUniversity, String newUniversity, String product) {
        Log.d("EasySpec", "Updating product: " + product);
        productDatabase.orderByChild("name").equalTo(product).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        decrementUserNum(oldUniversity, productSnapshot);
                        incrementUserNum(newUniversity, productSnapshot);
                    }
                } else {
                    Log.e("EasySpec", "Could not find the product: " + product);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("EasySpec", "Data update failed: " + databaseError.getMessage());
            }
        });
    }

    // 사용자 수를 감소시키는 메서드
    private void decrementUserNum(String university, DataSnapshot productSnapshot) {
        DatabaseReference userNumRef = productSnapshot.child("UserNum").getRef();
        Integer currentUserNum = productSnapshot.child("UserNum").child(university).getValue(Integer.class);
        if (currentUserNum == null) {
            currentUserNum = 0;
        }
        if (currentUserNum > 0) {
            userNumRef.child(university).setValue(currentUserNum - 1)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("EasySpec", "User number successfully decremented: " + university);
                        } else {
                            Log.e("EasySpec", "Failed to decrement user number: " + task.getException());
                        }
                    });
        }
    }

    // 사용자 수를 증가시키는 메서드
    private void incrementUserNum(String university, DataSnapshot productSnapshot) {
        DatabaseReference userNumRef = productSnapshot.child("UserNum").getRef();
        Integer currentUserNum = productSnapshot.child("UserNum").child(university).getValue(Integer.class);
        if (currentUserNum == null) {
            currentUserNum = 0;
        }
        userNumRef.child(university).setValue(currentUserNum + 1)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("EasySpec", "User number successfully incremented: " + university);
                    } else {
                        Log.e("EasySpec", "Failed to increment user number: " + task.getException());
                    }
                });
    }
}
