package com.example.easyspec.EachProductPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.R;

import java.util.List;

public class InnerReviewAdapter extends RecyclerView.Adapter<InnerReviewAdapter.InnerReviewViewHolder> {

    private List<InnerReviewItem> reviewItems;

    public InnerReviewAdapter(List<InnerReviewItem> reviewItems) {
        this.reviewItems = reviewItems;
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

        holder.goodButton.setOnClickListener(v -> {
            reviewItem.incrementGoodCount();
            holder.goodCount.setText(String.valueOf(reviewItem.getGoodCount()));
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
}
