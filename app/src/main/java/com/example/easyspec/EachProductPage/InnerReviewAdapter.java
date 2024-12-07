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
        holder.reviewText.setText("리뷰를 보려면 클릭하세요. 1포인트가 소모됩니다(최초 1회만)"); // 리뷰 내용은 기본적으로 숨김
        holder.goodCount.setText(String.valueOf(reviewItem.getGoodCount()));

        String reviewId = reviewItem.getReviewId();
        String feature = reviewItem.getFeature(); // feature 추가 필요
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        DatabaseReference productFeatureRef = userRef.child("usedFeatures").child(reviewItem.getProductId()).child(feature);

        // 리뷰 클릭 리스너
        holder.itemView.setOnClickListener(v -> {
            productFeatureRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean alreadyUsed = snapshot.exists() && snapshot.getValue(Boolean.class);

                    if (alreadyUsed) {
                        // 이미 포인트를 사용했음: 리뷰 표시
                        holder.reviewText.setText(reviewItem.getReviewText());
                    } else {
                        // 포인트 확인 및 차감
                        userRef.child("point").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot pointSnapshot) {
                                Long points = pointSnapshot.getValue(Long.class);
                                if (points != null && points >= 1) {
                                    // 포인트 차감 및 기록
                                    userRef.child("point").setValue(points - 1);
                                    productFeatureRef.setValue(true);
                                    holder.reviewText.setText(reviewItem.getReviewText());
                                } else {
                                    // 포인트 부족
                                    holder.reviewText.setText("포인트가 부족합니다.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("InnerReviewAdapter", "Failed to fetch user points", error.toException());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("InnerReviewAdapter", "Failed to fetch usedFeatures", error.toException());
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
