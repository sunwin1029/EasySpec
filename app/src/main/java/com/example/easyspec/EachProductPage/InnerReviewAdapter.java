package com.example.easyspec.EachProductPage;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class InnerReviewAdapter extends RecyclerView.Adapter<InnerReviewAdapter.InnerReviewViewHolder> {

    private List<InnerReviewItem> reviewItems;
    private String userId;

    public InnerReviewAdapter(List<InnerReviewItem> reviewItems, String userId) {
        this.reviewItems = reviewItems;
        this.userId = userId;
    }

    @NonNull
    @Override
    public InnerReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.each_product_property_review_expanded, parent, false);
        return new InnerReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerReviewViewHolder holder, int position) {
        InnerReviewItem reviewItem = reviewItems.get(position);

        holder.department.setText(reviewItem.getDepartment());
        holder.reviewText.setText(reviewItem.getReviewText());
        holder.goodCount.setText(String.valueOf(reviewItem.getGoodCount()));

        String reviewId = reviewItem.getReviewId();
        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("Reviews").child(reviewId);
        DatabaseReference userLikeRef = reviewRef.child("likedBy").child(userId);

        // 초기 좋아요 상태 확인
        userLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isLiked = snapshot.exists() && snapshot.getValue(Boolean.class);
                updateLikeButtonState(holder.goodButton, isLiked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("InnerReviewAdapter", "Failed to fetch like status", error.toException());
            }
        });

        // 좋아요 버튼 클릭 리스너 설정
        holder.goodButton.setOnClickListener(v -> {
            userLikeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isLiked = snapshot.exists() && snapshot.getValue(Boolean.class);

                    if (isLiked) {
                        // 좋아요 취소
                        reviewRef.child("likes").setValue(reviewItem.getGoodCount() - 1);
                        userLikeRef.removeValue();
                        reviewItem.decrementGoodCount();
                        updateLikeButtonState(holder.goodButton, false);
                    } else {
                        // 좋아요 추가
                        reviewRef.child("likes").setValue(reviewItem.getGoodCount() + 1);
                        userLikeRef.setValue(true);
                        reviewItem.incrementGoodCount();
                        updateLikeButtonState(holder.goodButton, true);
                    }

                    // 좋아요 개수 업데이트
                    holder.goodCount.setText(String.valueOf(reviewItem.getGoodCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("InnerReviewAdapter", "Failed to update like status", error.toException());
                }
            });
        });
    }



    @Override
    public int getItemCount() {
        return reviewItems.size();
    }

    public static class InnerReviewViewHolder extends RecyclerView.ViewHolder {
        TextView department, reviewText, goodCount;
        ImageView goodButton;

        public InnerReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            department = itemView.findViewById(R.id.InEachProductReviewDepartment);
            reviewText = itemView.findViewById(R.id.InEachProductReviewReviewText);
            goodCount = itemView.findViewById(R.id.InEachProductReviewGoodNum);
            goodButton = itemView.findViewById(R.id.InEachProductReviewGoodButton);
        }
    }

    private void updateLikeButtonState(ImageView button, boolean isLiked) {
        int color = isLiked
                ? ContextCompat.getColor(button.getContext(), R.color.blue) // 좋아요 상태일 때 파란색
                : ContextCompat.getColor(button.getContext(), R.color.black); // 기본 상태일 때 검정색
        button.setColorFilter(color);
    }

}
