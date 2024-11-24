package com.example.easyspec;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyReviewActivity extends AppCompatActivity {

    // UI 및 데이터 필드
    private RecyclerView recyclerView; //사용자 리뷰 표시
    private MyReviewAdapter adapter; //RecyclerView에 데이터를 바인딩 할 어댑터
    private List<ReviewItem> reviewList; // 리뷰 데이터를 저장할 리스트

    private FirebaseAuth auth; //Firebase 인증 객체
    private DatabaseReference databaseReference; // Firebase Database 참조
    private ValueEventListener reviewListener; // Firebase 데이터 변경 리스너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance(); //Firebase 인증 객체 가져오기
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //현재 로그인된 사용자의 UID가져오기
        String currentUserId = auth.getCurrentUser().getUid();
        //Firebase database에서 "reviews" 경로 참조
        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //RecyclerView 세로형 레이아웃으로 설정
        reviewList = new ArrayList<>(); //리뷰 데이터를 저장할 리스트 초기화
        adapter = new MyReviewAdapter(reviewList, this::onReviewItemClicked); //어댑터 초기화
        recyclerView.setAdapter(adapter); //RecyclerView 에 어댑터 연결

        // Firebase 데이터 가져오기 (실시간 업데이트)
        addReviewListener(currentUserId);
    }

    //리뷰 데이터를 실시간으로 가져오는 리스너를 추가하는 메서드
    private void addReviewListener(String userId) {
        reviewListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //firebase에서 데이터가 변경되었을 때 호출됨
                reviewList.clear(); //기존데이터 초기화
                if (dataSnapshot.exists()) { //데이터가 존재할 경우
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        // 각 제품에 대한 사용자의 리뷰 데이터를 확인
                        DataSnapshot userReviewSnapshot = productSnapshot.child(userId);
                        if (userReviewSnapshot.exists()) {
                            // 제품 ID 및 리뷰 데이터 가져오기
                            String productId = productSnapshot.getKey();
                            String batteryReview = userReviewSnapshot.child("batteryReview").getValue(String.class);
                            String performanceReview = userReviewSnapshot.child("performanceReview").getValue(String.class);

                            //ReviewItem 객체 생성 및 데이터 설정
                            ReviewItem item = new ReviewItem();
                            item.setKey(productId); // 제품 ID를 키로 저장
                            item.setBatteryReview(batteryReview);
                            item.setPerformanceReview(performanceReview);

                            // 리뷰 리스트에 추가
                            reviewList.add(item);
                        }
                    }
                    // 어댑터에 데이터 변경을 알림 (UI 업데이트)
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("MyReviewActivity", "No reviews found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyReviewActivity", "Failed to listen for reviews", databaseError.toException());
            }
        };

        // Firebase에서 데이터 변경을 실시간으로 감지
        databaseReference.addValueEventListener(reviewListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 리스너 제거
        if (reviewListener != null) {
            databaseReference.removeEventListener(reviewListener);
        }
    }

    private void onReviewItemClicked(ReviewItem reviewItem) {
        // 리뷰 아이템 클릭 시 프래그먼트 열기
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ReviewEditFragment fragment = ReviewEditFragment.newInstance(reviewItem.getKey());
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
