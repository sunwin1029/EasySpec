package com.example.easyspec.Profile.MyReview;

import android.os.Bundle;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);

        recyclerView = findViewById(R.id.recyclerView); // RecyclerView 초기화
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 레이아웃 매니저 설정

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
        if (isFetchingReviews) return; // 이미 데이터 요청 중이면 리턴
        isFetchingReviews = true; // 데이터 요청 시작

        // 사용자 ID에 해당하는 리뷰 가져오기
        reviewsRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reviewItemList.clear(); // 기존 데이터 삭제
                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                            ReviewItem reviewItem = reviewSnapshot.getValue(ReviewItem.class); // 리뷰 항목 가져오기
                            if (reviewItem != null) {
                                reviewItem.setKey(reviewSnapshot.getKey()); // 리뷰 항목에 키 설정

                                // ProductItems에서 제품 이름 가져오기
                                String productId = reviewItem.getProductId();
                                DatabaseReference productRef = FirebaseDatabase.getInstance()
                                        .getReference("ProductItems").child(productId);

                                productRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                                        String productName = productSnapshot.getValue(String.class); // 제품 이름 가져오기
                                        reviewItem.setName(productName); // 리뷰 항목에 제품 이름 설정

                                        reviewItemList.add(reviewItem); // 리뷰 리스트에 추가
                                        adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("EasySpec", "Error fetching product name: " + databaseError.getMessage()); // Error fetching product name log
                                    }
                                });
                            }
                        }
                        isFetchingReviews = false; // 데이터 요청 완료
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("EasySpec", "Error fetching reviews: " + databaseError.getMessage()); // Error fetching reviews log
                        isFetchingReviews = false; // 오류 발생 시에도 요청 완료 상태로 변경
                    }
                });
    }

    public void reloadReviews() {
        String userId = firebaseAuth.getCurrentUser().getUid(); // 현재 로그인된 사용자 ID 가져오기
        fetchUserReviews(userId); // 리뷰 새로 고침
    }
}
