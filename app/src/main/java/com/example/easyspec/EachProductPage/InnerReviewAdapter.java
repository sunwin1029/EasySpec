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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InnerReviewAdapter extends RecyclerView.Adapter<InnerReviewAdapter.InnerReviewViewHolder> {

    private List<InnerReviewItem> reviewItems;
    private String userId;
    private Map<String, Boolean> featureUsageMap = new HashMap<>(); // feature별 포인트 사용 여부


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

        // UI 초기화
        holder.reviewText.setText("리뷰를 불러오는 중...");
        updateLikeButtonState(holder.goodButton, false);

        // 데이터 설정
        holder.department.setText(reviewItem.getDepartment());
        holder.goodCount.setText(String.valueOf(reviewItem.getGoodCount()));

        // Firebase 참조
        String feature = reviewItem.getFeature();
        String productId = reviewItem.getProductId();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        DatabaseReference productFeatureRef = userRef.child("usedFeatures").child(productId).child(feature);

        // 포인트 사용 여부 확인
        if (featureUsageMap.containsKey(feature) && featureUsageMap.get(feature)) {
            // 이미 포인트가 사용된 feature
            holder.reviewText.setText(reviewItem.getReviewText());
        } else {
            // 포인트가 사용되지 않은 feature
            productFeatureRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean alreadyUsed = snapshot.exists() && snapshot.getValue(Boolean.class);

                    if (alreadyUsed) {
                        // 포인트가 이미 사용된 경우
                        featureUsageMap.put(feature, true);
                        holder.reviewText.setText(reviewItem.getReviewText());
                    } else {
                        // 포인트가 사용되지 않은 경우
                        holder.reviewText.setText("리뷰를 보려면 클릭하세요. 1포인트가 소모됩니다.");
                        holder.itemView.setOnClickListener(v -> {
                            deductPointsAndShowReviews(userRef, productFeatureRef, feature);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("InnerReviewAdapter", "Failed to fetch usedFeatures", error.toException());
                }
            });
        }

        // 좋아요 버튼 클릭 리스너
        holder.goodButton.setOnClickListener(v -> handleLikeButtonClick(
                FirebaseDatabase.getInstance().getReference("Reviews").child(reviewItem.getReviewId()).child("likedBy").child(userId),
                FirebaseDatabase.getInstance().getReference("Reviews").child(reviewItem.getReviewId()),
                holder, reviewItem));
    }


    @Override
    public int getItemCount() {
        return reviewItems.size();
    }

    private void deductPointsAndShowReviews(DatabaseReference userRef, DatabaseReference productFeatureRef, String feature) {
        userRef.child("point").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long points = snapshot.getValue(Long.class);
                if (points != null && points >= 1) {
                    // 포인트 차감
                    userRef.child("point").setValue(points - 1);
                    productFeatureRef.setValue(true); // Firebase에 포인트 사용 상태 업데이트

                    // 로컬 상태 업데이트
                    featureUsageMap.put(feature, true);

                    // RecyclerView 갱신
                    for (int i = 0; i < reviewItems.size(); i++) {
                        if (reviewItems.get(i).getFeature().equals(feature)) {
                            notifyItemChanged(i); // 해당 feature의 모든 리뷰를 갱신
                        }
                    }
                } else {
                    Log.e("InnerReviewAdapter", "포인트가 부족합니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("InnerReviewAdapter", "Failed to fetch points", error.toException());
            }
        });
    }


    private void handleLikeButtonClick(DatabaseReference userLikeRef, DatabaseReference reviewRef,
                                       InnerReviewViewHolder holder, InnerReviewItem reviewItem) {
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
    }

    private void updateLikeButtonState(ImageView button, boolean isLiked) {
        int color = isLiked
                ? ContextCompat.getColor(button.getContext(), R.color.blue) // 좋아요 상태일 때 파란색
                : ContextCompat.getColor(button.getContext(), R.color.black); // 기본 상태일 때 검정색
        button.setColorFilter(color);
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
}
