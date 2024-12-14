package com.example.easyspec.Profile;

import static android.content.ContentValues.TAG;

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
    private DatabaseReference productDatabase; // ProductItems 데이터베이스 참조 추가
    private Button buttonUpdate;
    private TextView textViewUserEmail;
    private String currentUniversity; // 현재 대학 정보를 저장할 변수
    private TextView pointValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        productDatabase = FirebaseDatabase.getInstance().getReference("ProductItems"); // ProductItems 참조 초기화

        spinnerUniversity = findViewById(R.id.universitySpinner);
        buttonUpdate = findViewById(R.id.saveButton);
        textViewUserEmail = findViewById(R.id.newTextBelowUserId);
        pointValue =findViewById(R.id.pointsValue);

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

                        // 현재 대학교 정보 저장
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

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    private void updateUserInfo() {
        String university = spinnerUniversity.getSelectedItem().toString();
        String userId = mAuth.getCurrentUser().getUid();

        // Firebase에서 해당 사용자 정보 수정
        mDatabase.child(userId).child("university").setValue(university).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 대학이 변경되었는지 확인
                if (!currentUniversity.equals(university)) {
                    // UserNum 업데이트
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

    private void updateUserNum(String userId, String oldUniversity, String newUniversity) {
        Log.d(TAG, "업데이트할 사용자 ID: " + userId + ", 이전 대학: " + oldUniversity + ", 새 대학: " + newUniversity);

        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String laptop = dataSnapshot.child("laptop").getValue(String.class);
                    String tablet = dataSnapshot.child("tablet").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);

                    // 각 기기에 대해 사용자 수 업데이트
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

    private void updateDeviceUserNum(String oldUniversity, String newUniversity, String product) {
        Log.d(TAG, "업데이트할 제품: " + product);

        // productDatabase에서 해당 제품 이름으로 검색합니다
        productDatabase.orderByChild("name").equalTo(product).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        // 이전 대학 사용자 수 감소
                        decrementUserNum(oldUniversity, productSnapshot);
                        // 새 대학 사용자 수 증가
                        incrementUserNum(newUniversity, productSnapshot);
                    }
                } else {
                    Log.e(TAG, "해당 제품을 찾을 수 없습니다: " + product);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "데이터 업데이트 실패: " + databaseError.getMessage());
            }
        });
    }

    private void decrementUserNum(String university, DataSnapshot productSnapshot) {
        DatabaseReference userNumRef = productSnapshot.child("UserNum").getRef();
        Integer currentUserNum = productSnapshot.child("UserNum").child(university).getValue(Integer.class);
        if (currentUserNum == null) {
            currentUserNum = 0; // 값이 null일 경우 0으로 초기화
        }
        // UserNum이 0보다 클 때만 감소
        if (currentUserNum > 0) {
            userNumRef.child(university).setValue(currentUserNum - 1)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "사용자 수가 성공적으로 감소되었습니다: " + university);
                        } else {
                            Log.e(TAG, "사용자 수 감소 실패: " + task.getException());
                        }
                    });
        }
    }

    private void incrementUserNum(String university, DataSnapshot productSnapshot) {
        DatabaseReference userNumRef = productSnapshot.child("UserNum").getRef();
        Integer currentUserNum = productSnapshot.child("UserNum").child(university).getValue(Integer.class);
        if (currentUserNum == null) {
            currentUserNum = 0; // 값이 null일 경우 0으로 초기화
        }
        // 사용자 수를 +1 합니다
        userNumRef.child(university).setValue(currentUserNum + 1)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "사용자 수가 성공적으로 증가되었습니다: " + university);
                    } else {
                        Log.e(TAG, "사용자 수 증가 실패: " + task.getException());
                    }
                });
    }
}
