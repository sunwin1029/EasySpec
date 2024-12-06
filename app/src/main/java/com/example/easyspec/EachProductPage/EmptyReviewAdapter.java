package com.example.easyspec.EachProductPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.R;

public class EmptyReviewAdapter extends RecyclerView.Adapter<EmptyReviewAdapter.EmptyReviewViewHolder> {

    @NonNull
    @Override
    public EmptyReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.each_product_property_review_expanded, parent, false);
        return new EmptyReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmptyReviewViewHolder holder, int position) {
        // 현재는 빈 상태 유지
    }

    @Override
    public int getItemCount() {
        return 0; // 빈 RecyclerView
    }

    public static class EmptyReviewViewHolder extends RecyclerView.ViewHolder {
        public EmptyReviewViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
