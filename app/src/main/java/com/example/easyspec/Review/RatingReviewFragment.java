package com.example.easyspec.Review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easyspec.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RatingReviewFragment extends Fragment {

    private String userId;
    private String productId;
    private RatingBar ratingBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating_review, container, false);

        // 전달받은 데이터 가져오기
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            productId = getArguments().getString("productId");
        }

        ratingBar = view.findViewById(R.id.ratingBar);

        // "formerButton" 클릭 시
        view.findViewById(R.id.formerButton).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // "nextButton" 클릭 시
        view.findViewById(R.id.nextButton).setOnClickListener(v -> saveRatingAndReturn());

        return view;
    }

    private void saveRatingAndReturn() {
        float rating = ratingBar.getRating();

        if (rating == 0) {
            Toast.makeText(getContext(), "평점을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference productRef = FirebaseDatabase.getInstance()
                .getReference("ProductItems")
                .child(productId)
                .child("rating");

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int ratingCount = snapshot.child("ratingCount").getValue(Integer.class) != null ?
                        snapshot.child("ratingCount").getValue(Integer.class) : 0;
                double totalRating = snapshot.child("totalRating").getValue(Double.class) != null ?
                        snapshot.child("totalRating").getValue(Double.class) : 0.0;

                // 기존 평점 확인
                Double existingRating = snapshot.child("ratingDetails").child(userId).getValue(Double.class);

                if (existingRating != null) {
                    // 기존 평점 제거
                    totalRating -= existingRating;
                } else {
                    // 새로운 평점 추가 시 카운트 증가
                    ratingCount++;
                }

                // 새 평점 반영
                totalRating += rating;

                // Firebase에 업데이트
                productRef.child("ratingCount").setValue(ratingCount);
                productRef.child("totalRating").setValue(totalRating);
                productRef.child("ratingDetails").child(userId).setValue(rating);

                Toast.makeText(getContext(), "평점이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "평점 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
