package com.example.easyspec;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.Firebase.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyReviewActivity extends AppCompatActivity {

    // UI 및 데이터 필드
    private RecyclerView recyclerView; // 사용자 리뷰 표시
    private MyReviewAdapter adapter; // RecyclerView에 데이터를 바인딩할 어댑터
    private List<ReviewItem> reviewList; // 리뷰 데이터를 저장할 리스트

    private String currentUserId; // 현재 사용자 ID
    private DatabaseReference reviewsRef; // Firebase Database 참조
    private ValueEventListener realtimeListener; // 실시간 데이터 변경 리스너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);

        // Firebase 인증 객체 가져오기
        currentUserId = FirebaseHelper.getCurrentUserId();
        if (currentUserId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // RecyclerView 세로형 레이아웃으로 설정
        reviewList = new ArrayList<>(); // 리뷰 데이터를 저장할 리스트 초기화
        adapter = new MyReviewAdapter(reviewList, this::onReviewItemClicked); // 어댑터 초기화
        recyclerView.setAdapter(adapter); // RecyclerView에 어댑터 연결

        // Firebase Database 경로 설정
        reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

        // 초기 리뷰 데이터를 로드
        loadUserReviews();

        // 실시간 업데이트 리스너 추가
        addRealtimeListener();
    }

    // 특정 사용자의 리뷰 데이터를 FirebaseHelper를 통해 가져오는 메서드
    private void loadUserReviews() {
        FirebaseHelper.getInstance().getUserReviews(
                currentUserId,
                new FirebaseHelper.ReviewCallback() {
                    @Override
                    public void onSuccess(List<ReviewItem> reviews) {
                        // 리뷰 리스트를 업데이트하고 UI에 반영
                        reviewList.clear();
                        reviewList.addAll(reviews);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // 에러 처리
                        Log.e("MyReviewActivity", "리뷰 데이터를 가져오는 중 오류 발생", e);
                        Toast.makeText(MyReviewActivity.this, "리뷰를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * 실시간 업데이트 리스너 추가
     */
    private void addRealtimeListener() {
        realtimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 실시간 데이터 변경 감지
                reviewList.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot userReviewSnapshot = productSnapshot.child(currentUserId);
                    if (userReviewSnapshot.exists()) {
                        String productId = productSnapshot.getKey();
                        String batteryReview = userReviewSnapshot.child("batteryReview").getValue(String.class);
                        String performanceReview = userReviewSnapshot.child("performanceReview").getValue(String.class);

                        // ReviewItem 생성
                        ReviewItem item = new ReviewItem();
                        item.setKey(productId);
                        item.setBatteryReview(batteryReview);
                        item.setPerformanceReview(performanceReview);

                        reviewList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyReviewActivity", "실시간 업데이트 실패", databaseError.toException());
            }
        };

        // Firebase Database에서 실시간 리스너 등록
        reviewsRef.addValueEventListener(realtimeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 리스너 제거
        if (realtimeListener != null) {
            reviewsRef.removeEventListener(realtimeListener);
        }
    }

    private void onReviewItemClicked(ReviewItem reviewItem) {
        // 리뷰 아이템 클릭 시 프래그먼트 열기
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MyReviewEditFragment fragment = MyReviewEditFragment.newInstance(reviewItem.getKey());
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
