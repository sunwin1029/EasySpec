package com.example.easyspec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ViewHolder> {
    private List<ReviewItem> reviewItemList;
    private Context context;

    public MyReviewAdapter(List<ReviewItem> reviewItemList, Context context) {
        this.reviewItemList = reviewItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewItem reviewItem = reviewItemList.get(position);

        // 제품 이미지 설정 (이미지 로딩 라이브러리 사용)
        holder.productImage.setImageResource(reviewItem.getImageResource());
        holder.productName.setText(reviewItem.getProductName());
        holder.reviewText.setText(reviewItem.getReviewText());
        holder.reviewScore.setText(String.valueOf(reviewItem.getReviewScore()));

        // 수정 버튼 클릭 리스너
        holder.btnEdit.setOnClickListener(v -> {
            // 수정하기 위한 로직 (예: ReviewEditFragment로 이동)
            // 예: openEditFragment(reviewItem.getKey());
        });

        // 삭제 버튼 클릭 리스너
        holder.btnDelete.setOnClickListener(v -> {
            // 삭제하기 위한 로직
            // 예: deleteReview(reviewItem.getKey());
        });
    }

    @Override
    public int getItemCount() {
        return reviewItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, reviewText, reviewScore;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            reviewText = itemView.findViewById(R.id.review_text);
            reviewScore = itemView.findViewById(R.id.review_score);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

