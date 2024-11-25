package com.example.easyspec;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewWriteActivity extends AppCompatActivity {

    private EditText batteryReviewEditText;
    private EditText performanceReviewEditText;
    private Button submitReviewButton;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");

        // UI 초기화
        batteryReviewEditText = findViewById(R.id.batteryReviewEditText);
        performanceReviewEditText = findViewById(R.id.performanceReviewEditText);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        // Intent에서 제품 이름 받기
        String productName = getIntent().getStringExtra("productName");

        // 리뷰 저장 버튼 클릭 시
        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReview(productName);
            }
        });
    }

    private void saveReview(String productName) {
        String batteryReview = batteryReviewEditText.getText().toString().trim();
        String performanceReview = performanceReviewEditText.getText().toString().trim();

        if (batteryReview.isEmpty() || performanceReview.isEmpty()) {
            Toast.makeText(this, "모든 리뷰 항목을 작성해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        DatabaseReference productReviewRef = databaseReference.child(productName).child(userId);

        // 리뷰 데이터를 Firebase에 저장
        productReviewRef.child("batteryReview").setValue(batteryReview);
        productReviewRef.child("performanceReview").setValue(performanceReview)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ReviewWriteActivity.this, "리뷰가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 리뷰 저장 후 이전 화면으로 돌아가기
                    } else {
                        Toast.makeText(ReviewWriteActivity.this, "리뷰 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
