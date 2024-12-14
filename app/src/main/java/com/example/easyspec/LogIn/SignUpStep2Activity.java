package com.example.easyspec.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.R;
import com.example.easyspec.Data.Users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignUpStep2Activity extends AppCompatActivity {
    private static final String TAG = "SignUpStep2Activity"; // 로그 태그
    private DatabaseReference productDatabase;
    private DatabaseReference userDatabase;
    private Spinner spinnerLaptop, spinnerTablet, spinnerPhone;
    private Button buttonFinish;
    private List<String> laptopList = new ArrayList<>();
    private List<String> tabletList = new ArrayList<>();
    private List<String> phoneList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_step2);

        // Firebase 데이터베이스 참조 초기화
        productDatabase = FirebaseDatabase.getInstance().getReference("ProductItems");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        initializeUI(); // UI 초기화
        loadProductData(); // 제품 데이터 로드
    }

    private void initializeUI() {
        // UI 요소 연결
        spinnerLaptop = findViewById(R.id.spinnerLaptop);
        spinnerTablet = findViewById(R.id.spinnerTablet);
        spinnerPhone = findViewById(R.id.spinnerPhone);
        buttonFinish = findViewById(R.id.buttonFinish);

        // 버튼 클릭 시 registerUser 메서드 호출
        buttonFinish.setOnClickListener(v -> registerUser());
    }

    private void loadProductData() {
        // 제품 데이터 로드
        productDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                laptopList.clear();
                tabletList.clear();
                phoneList.clear();

                // 제품 정보를 가져와서 리스트에 추가
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String name = productSnapshot.child("name").getValue(String.class);
                    int productType = productSnapshot.child("productType").getValue(Integer.class);

                    switch (productType) {
                        case 1:
                            laptopList.add(name);
                            break;
                        case 2:
                            tabletList.add(name);
                            break;
                        case 3:
                            phoneList.add(name);
                            break;
                    }
                }

                // 스피너에 데이터 설정
                setSpinnerData(spinnerLaptop, laptopList);
                setSpinnerData(spinnerTablet, tabletList);
                setSpinnerData(spinnerPhone, phoneList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignUpStep2Activity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerData(Spinner spinner, List<String> dataList) {
        // 스피너에 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void registerUser() {
        // 인텐트에서 사용자 ID와 이메일 가져오기
        String userId = getIntent().getStringExtra("userId"); // UID 받기
        String email = getIntent().getStringExtra("email");

        if (userId == null || email == null) {
            Toast.makeText(this, "Email or user ID is missing.", Toast.LENGTH_SHORT).show();
            return; // 이메일이나 사용자 ID가 없으면 메서드 종료
        }

        // 추가 정보 저장 메서드 호출
        saveUserInfo(userId, email);
    }

    private void saveUserInfo(String userId, String email) {
        // 선택한 대학과 기기 정보 가져오기
        String university = getIntent().getStringExtra("university");
        String laptop = getSelectedItem(spinnerLaptop);
        String tablet = getSelectedItem(spinnerTablet);
        String phone = getSelectedItem(spinnerPhone);

        if (laptop == null && tablet == null && phone == null) {
            Toast.makeText(this, "At least one device must be selected.", Toast.LENGTH_SHORT).show();
            return; // 기기를 선택하지 않았으면 메서드 종료
        }

        int initialPoint = 0;
        Users user = new Users(email, university, laptop, tablet, phone, initialPoint);

        Log.d("EasySpec", "Saving user information: userId = " + userId); // 사용자 정보 저장 로그

        userDatabase.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpStep2Activity.this, "Sign up completed.", Toast.LENGTH_SHORT).show();

                // 사용자 수 업데이트
                if (laptop != null) {
                    updateUserNum(laptop, university); // 스피너에서 선택한 노트북의 UserNum 업데이트
                }
                if (tablet != null) {
                    updateUserNum(tablet, university); // 스피너에서 선택한 태블릿의 UserNum 업데이트
                }
                if (phone != null) {
                    updateUserNum(phone, university); // 스피너에서 선택한 핸드폰의 UserNum 업데이트
                }

                Intent intent = new Intent(SignUpStep2Activity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Log.e("EasySpec", "Failed to save user information", task.getException()); // 실패 로그
                Toast.makeText(SignUpStep2Activity.this, "Failed to save user information: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUserNum(String productName, String university) {
        Log.d("EasySpec", "Product to update: " + productName + ", University: " + university); // 업데이트할 제품 로그

        // productDatabase에서 해당 제품 이름으로 검색합니다
        productDatabase.orderByChild("name").equalTo(productName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        // 전공별 사용자 수를 가져옵니다
                        DatabaseReference userNumRef = productSnapshot.child("UserNum").getRef();
                        Integer currentUserNum = productSnapshot.child("UserNum").child(university).getValue(Integer.class);
                        if (currentUserNum == null) {
                            currentUserNum = 0; // 값이 null일 경우 0으로 초기화
                        }

                        // 사용자 수를 +1 합니다
                        userNumRef.child(university).setValue(currentUserNum + 1)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("EasySpec", "User count successfully updated: " + university); // 성공 로그
                                    } else {
                                        Log.e("EasySpec", "Failed to update user count: " + task.getException()); // 실패 로그
                                    }
                                });
                    }
                } else {
                    Log.e("EasySpec", "Product not found: " + productName); // 제품 미발견 로그
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignUpStep2Activity.this, "Failed to update user count.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSelectedItem(Spinner spinner) {
        // 선택된 아이템 반환
        return spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : null;
    }
}
