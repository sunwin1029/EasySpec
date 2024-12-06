package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.Data.SearchData;
import com.example.easyspec.databinding.ActivityIntentTestBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IntentTest extends AppCompatActivity {

    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityIntentTestBinding binding = ActivityIntentTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase Users 노드 참조
        userReference = FirebaseDatabase.getInstance().getReference("Users");

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 두 번째 유저의 ID를 가져오기
                fetchSecondUserId(userId -> {
                    if (userId != null) {
                        // SearchData 객체 생성
                        SearchData searchData = new SearchData(
                                -1,              // 제품 유형: 예시로 "노트북" 설정
                                null,        // 이름: 예시로 "Galaxy" 설정
                                1000000,          // 최소 가격
                                -1,         // 최대 가격
                                -1                // 회사: 삼성 (1)
                        );

                        // Intent 생성 및 데이터 전달
                        Intent intent = new Intent(IntentTest.this, InventoryProductPage.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("searchData", searchData);
                        startActivity(intent);
                    } else {
                        Toast.makeText(IntentTest.this, "두 번째 유저를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // 두 번째 유저의 ID를 가져오는 메서드
    private void fetchSecondUserId(UserIdCallback callback) {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (index == 1) { // 두 번째 유저
                        String userId = userSnapshot.getKey();
                        callback.onUserIdFetched(userId);
                        return;
                    }
                    index++;
                }
                callback.onUserIdFetched(null); // 두 번째 유저가 없는 경우
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onUserIdFetched(null);
            }
        });
    }

    // Callback 인터페이스 정의
    interface UserIdCallback {
        void onUserIdFetched(String userId);
    }
}
