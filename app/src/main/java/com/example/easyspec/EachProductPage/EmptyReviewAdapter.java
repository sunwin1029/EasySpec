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
        View view = inflater.inflate(R.layout.each_product_property_empty, parent, false); // 빈 상태를 표시하는 XML 사용
        return new EmptyReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmptyReviewViewHolder holder, int position) {
        // 추가 작업 필요 없음
    }

    @Override
    public int getItemCount() {
        return 1; // 항상 하나의 항목 렌더링
    }

    public static class EmptyReviewViewHolder extends RecyclerView.ViewHolder {
        public EmptyReviewViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

