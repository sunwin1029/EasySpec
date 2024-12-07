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
    private static final String TAG = "SignUpStep2Activity";
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

        productDatabase = FirebaseDatabase.getInstance().getReference("ProductItems");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        initializeUI();
        loadProductData();
    }

    private void initializeUI() {
        spinnerLaptop = findViewById(R.id.spinnerLaptop);
        spinnerTablet = findViewById(R.id.spinnerTablet);
        spinnerPhone = findViewById(R.id.spinnerPhone);
        buttonFinish = findViewById(R.id.buttonFinish);

        buttonFinish.setOnClickListener(v -> registerUser());
    }

    private void loadProductData() {
        productDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                laptopList.clear();
                tabletList.clear();
                phoneList.clear();

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

                setSpinnerData(spinnerLaptop, laptopList);
                setSpinnerData(spinnerTablet, tabletList);
                setSpinnerData(spinnerPhone, phoneList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignUpStep2Activity.this, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerData(Spinner spinner, List<String> dataList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void registerUser() {
        String userId = getIntent().getStringExtra("userId"); // UID 받기
        String email = getIntent().getStringExtra("email");

        if (userId == null || email == null) {
            Toast.makeText(this, "이메일 또는 사용자 ID가 없습니다.", Toast.LENGTH_SHORT).show();
            return; // 이메일이나 사용자 ID가 없으면 메서드 종료
        }

        // 추가 정보 저장 메서드 호출
        saveUserInfo(userId, email);
    }

    private void saveUserInfo(String userId, String email) {
        String university = getIntent().getStringExtra("university");
        String laptop = getSelectedItem(spinnerLaptop);
        String tablet = getSelectedItem(spinnerTablet);
        String phone = getSelectedItem(spinnerPhone);

        if (laptop == null && tablet == null && phone == null) {
            Toast.makeText(this, "최소 하나의 기기를 선택해야 합니다.", Toast.LENGTH_SHORT).show();
            return; // 기기를 선택하지 않았으면 메서드 종료
        }

        Users user = new Users(email, university, laptop, tablet, phone);

        Log.d(TAG, "Saving user info for userId: " + userId);

        userDatabase.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpStep2Activity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

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
                Log.e(TAG, "Failed to save user info", task.getException());
                Toast.makeText(SignUpStep2Activity.this, "회원정보 저장 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void updateUserNum(String productName, String university) {
        Log.d(TAG, "업데이트할 제품 이름: " + productName + ", 대학: " + university);

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
                                        Log.d(TAG, "사용자 수가 성공적으로 업데이트되었습니다: " + university);
                                    } else {
                                        Log.e(TAG, "사용자 수 업데이트 실패: " + task.getException());
                                    }
                                });
                    }
                } else {
                    Log.e(TAG, "해당 제품을 찾을 수 없습니다: " + productName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignUpStep2Activity.this, "사용자 수 업데이트 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String getSelectedItem(Spinner spinner) {
        return spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : null;
    }
}
