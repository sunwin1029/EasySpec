package com.example.easyspec.Profile.MyReview;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView; // RecyclerView
    private MyReviewAdapter adapter; // 리뷰 어댑터
    private List<ReviewItem> reviewItemList; // ReviewItem 목록
    private FirebaseAuth firebaseAuth; // Firebase 인증
    private DatabaseReference reviewsRef; // 리뷰 데이터베이스 참조
    private TextView emptyView; // 리뷰가 없을 때 표시할 TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);

        recyclerView = findViewById(R.id.recyclerView); // RecyclerView 초기화
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 레이아웃 매니저 설정

        emptyView = findViewById(R.id.emptyView);
        reviewItemList = new ArrayList<>(); // 리뷰 목록 초기화
        adapter = new MyReviewAdapter(reviewItemList, this); // 어댑터 초기화
        recyclerView.setAdapter(adapter); // RecyclerView에 어댑터 설정

        firebaseAuth = FirebaseAuth.getInstance(); // Firebase 인증 인스턴스 가져오기
        String userId = firebaseAuth.getCurrentUser().getUid(); // 현재 로그인된 사용자 ID 가져오기
        reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews"); // 리뷰 데이터베이스 참조 설정

        // 현재 로그인된 사용자의 리뷰 가져오기
        fetchUserReviews(userId);
    }

    private boolean isFetchingReviews = false; // 리뷰를 가져오는 중인지 여부를 추적하는 변수

    private void fetchUserReviews(String userId) {
        reviewsRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> currentKeys = new ArrayList<>(); // 현재 스냅샷에서 키를 추적
                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                            String key = reviewSnapshot.getKey();
                            currentKeys.add(key); // 현재 데이터의 키 추가

                            // 기존 리스트에 없는 경우만 추가
                            ReviewItem reviewItem = reviewSnapshot.getValue(ReviewItem.class);
                            if (reviewItem != null && key != null && !containsReview(key)) {
                                reviewItem.setKey(key);

                                // 제품 이름 가져오기
                                String productId = reviewItem.getProductId();
                                DatabaseReference productRef = FirebaseDatabase.getInstance()
                                        .getReference("ProductItems").child(productId);

                                productRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                                        String productName = productSnapshot.getValue(String.class);
                                        reviewItem.setName(productName);

                                        reviewItemList.add(reviewItem); // 새로운 리뷰 추가
                                        adapter.notifyItemInserted(reviewItemList.size() - 1);
                                        toggleEmptyView(); // 빈 상태 업데이트
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("EasySpec", "Error fetching product name: " + databaseError.getMessage());
                                    }
                                });
                            }
                        }

                        // 삭제된 항목 처리
                        removeDeletedReviews(currentKeys);

                        // 빈 상태 업데이트
                        toggleEmptyView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("EasySpec", "Error fetching reviews: " + databaseError.getMessage());
                    }
                });
    }

    // 리뷰 리스트에서 특정 키가 존재하는지 확인
    private boolean containsReview(String key) {
        for (ReviewItem item : reviewItemList) {
            if (item.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    // 현재 키에 없는 리뷰를 리스트에서 제거
    private void removeDeletedReviews(List<String> currentKeys) {
        List<ReviewItem> toRemove = new ArrayList<>();
        for (ReviewItem item : reviewItemList) {
            if (!currentKeys.contains(item.getKey())) {
                int position = reviewItemList.indexOf(item);
                toRemove.add(item);
                adapter.notifyItemRemoved(position);
            }
        }
        reviewItemList.removeAll(toRemove);
    }


    public void reloadReviews() {
        String userId = firebaseAuth.getCurrentUser().getUid(); // 현재 로그인된 사용자 ID 가져오기
        fetchUserReviews(userId); // 리뷰 새로 고침
    }


    private void toggleEmptyView() {
        runOnUiThread(() -> {
            if (reviewItemList.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE); // 리뷰가 없을 때 메시지 표시
                recyclerView.setVisibility(View.GONE); // RecyclerView 숨기기
            } else {
                emptyView.setVisibility(View.GONE); // 메시지 숨기기
                recyclerView.setVisibility(View.VISIBLE); // RecyclerView 표시
            }
        });
    }
}
