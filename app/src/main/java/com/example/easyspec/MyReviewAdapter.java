package com.example.easyspec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ReviewViewHolder> {

    private final List<ReviewItem> reviewList;
    private final OnReviewClickListener onReviewClickListener;

    // 클릭 리스너 인터페이스 정의
    public interface OnReviewClickListener {
        void onReviewClick(ReviewItem reviewItem);
    }

    // 생성자
    public MyReviewAdapter(List<ReviewItem> reviewList, OnReviewClickListener onReviewClickListener) {
        this.reviewList = reviewList;
        this.onReviewClickListener = onReviewClickListener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewItem reviewItem = reviewList.get(position);

        // 데이터 설정
        holder.productName.setText("Product: " + reviewItem.getKey());
        holder.batteryReview.setText("Battery Review: " + reviewItem.getBatteryReview());
        holder.performanceReview.setText("Performance Review: " + reviewItem.getPerformanceReview());

        // 클릭 이벤트
        holder.itemView.setOnClickListener(v -> onReviewClickListener.onReviewClick(reviewItem));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    // ViewHolder 클래스
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView productName, batteryReview, performanceReview;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textProductName);
            batteryReview = itemView.findViewById(R.id.textBatteryReview);
            performanceReview = itemView.findViewById(R.id.textPerformanceReview);
        }
    }
}
