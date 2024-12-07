package com.example.easyspec;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.Review.ReviewFragment;
import com.example.easyspec.ReviewItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyReviewActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private MyReviewAdapter adapter;
    private List<ReviewItem> reviewItemList; // ReviewItem 목록
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reviewsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reviewItemList = new ArrayList<>();
        adapter = new MyReviewAdapter(reviewItemList, this);
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews");

        // 현재 로그인된 사용자의 리뷰 가져오기
        fetchUserReviews(userId);
    }

    private void fetchUserReviews(String userId) {
        reviewsRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reviewItemList.clear(); // 기존 데이터 삭제
                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                            ReviewItem reviewItem = reviewSnapshot.getValue(ReviewItem.class);
                            if (reviewItem != null) {
                                reviewItem.setKey(reviewSnapshot.getKey()); // 키 설정
                                reviewItemList.add(reviewItem);
                            }
                        }
                        adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 오류 처리
                    }
                });
    }

}
