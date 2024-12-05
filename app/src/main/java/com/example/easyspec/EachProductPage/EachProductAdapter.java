package com.example.easyspec.EachProductPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.R;
import com.example.easyspec.Review.ReviewFragment;

import java.util.ArrayList;
import java.util.List;

public class EachProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_COLLAPSED = 0;
    private static final int TYPE_EXPANDED = 1;

    private List<String> featureList;
    private List<Boolean> expandedStates;
    private String productId;
    private String userId;

    public EachProductAdapter(List<String> featureList, String productId, String userId) {
        this.featureList = featureList;
        this.productId = productId;
        this.userId = userId;

        // 초기 상태: 모두 축소
        this.expandedStates = new ArrayList<>();
        for (int i = 0; i < featureList.size(); i++) {
            expandedStates.add(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return expandedStates.get(position) ? TYPE_EXPANDED : TYPE_COLLAPSED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_EXPANDED) {
            View view = inflater.inflate(R.layout.each_product_property_expanded, parent, false);
            return new ExpandedViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.each_product_property, parent, false);
            return new CollapsedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String feature = featureList.get(position);

        if (holder instanceof ExpandedViewHolder) {
            ExpandedViewHolder expandedHolder = (ExpandedViewHolder) holder;
            expandedHolder.featureText.setText(feature);

            // 리뷰 작성 버튼 클릭 리스너 설정
            expandedHolder.reviewButton.setOnClickListener(v -> {
                ReviewFragment fragment = ReviewFragment.newInstance(productId, feature, userId); // userId 추가
                ((AppCompatActivity) expandedHolder.itemView.getContext())
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            });

            // 축소 버튼 클릭 리스너
            expandedHolder.expandButton.setOnClickListener(v -> {
                expandedStates.set(position, false);
                notifyItemChanged(position);
            });

        } else if (holder instanceof CollapsedViewHolder) {
            CollapsedViewHolder collapsedHolder = (CollapsedViewHolder) holder;
            collapsedHolder.featureText.setText(feature);

            // 확장 버튼 클릭 리스너
            collapsedHolder.expandButton.setOnClickListener(v -> {
                expandedStates.set(position, true);
                notifyItemChanged(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    // ViewHolder for collapsed state
    static class CollapsedViewHolder extends RecyclerView.ViewHolder {
        TextView featureText;
        View expandButton;

        public CollapsedViewHolder(@NonNull View itemView) {
            super(itemView);
            featureText = itemView.findViewById(R.id.property);
            expandButton = itemView.findViewById(R.id.expandButton);
        }
    }

    // ViewHolder for expanded state
    static class ExpandedViewHolder extends RecyclerView.ViewHolder {
        TextView featureText;
        View expandButton;
        Button reviewButton;

        public ExpandedViewHolder(@NonNull View itemView) {
            super(itemView);
            featureText = itemView.findViewById(R.id.property);
            expandButton = itemView.findViewById(R.id.expandButton);
            reviewButton = itemView.findViewById(R.id.reviewButton); // 초기화
        }
    }
}
