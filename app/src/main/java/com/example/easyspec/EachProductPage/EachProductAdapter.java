package com.example.easyspec.EachProductPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.R;

import java.util.ArrayList;
import java.util.List;

public class EachProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_COLLAPSED = 0;
    private static final int TYPE_EXPANDED = 1;

    private List<String> featureList;
    private List<Boolean> expandedStates;

    public EachProductAdapter(List<String> featureList) {
        this.featureList = featureList;
        this.expandedStates = new ArrayList<>();
        for (int i = 0; i < featureList.size(); i++) {
            expandedStates.add(false); // 초기 상태는 모두 축소
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
        boolean isExpanded = expandedStates.get(position);

        if (holder instanceof ExpandedViewHolder) {
            ExpandedViewHolder expandedHolder = (ExpandedViewHolder) holder;

            expandedHolder.propertyTextView.setText(feature);

            // RecyclerView 설정 (현재는 빈 상태)
            expandedHolder.expandedRecyclerView.setLayoutManager(new LinearLayoutManager(expandedHolder.itemView.getContext()));
            expandedHolder.expandedRecyclerView.setAdapter(new EmptyReviewAdapter());

            // 확장 상태 버튼 이미지 설정
            expandedHolder.expandButton.setImageResource(R.drawable.arrow_up);

            // 축소 버튼 동작
            expandedHolder.expandButton.setOnClickListener(v -> {
                expandedStates.set(position, false);
                notifyItemChanged(position);
            });

        } else if (holder instanceof CollapsedViewHolder) {
            CollapsedViewHolder collapsedHolder = (CollapsedViewHolder) holder;

            collapsedHolder.propertyTextView.setText(feature);

            // 축소 상태 버튼 이미지 설정
            collapsedHolder.expandButton.setImageResource(R.drawable.arrow_down);

            // 확장 버튼 동작
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
        TextView propertyTextView;
        ImageView expandButton;

        public CollapsedViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyTextView = itemView.findViewById(R.id.property);
            expandButton = itemView.findViewById(R.id.expandButton);
        }
    }

    // ViewHolder for expanded state
    static class ExpandedViewHolder extends RecyclerView.ViewHolder {
        TextView propertyTextView;
        ImageView expandButton;
        RecyclerView expandedRecyclerView;

        public ExpandedViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyTextView = itemView.findViewById(R.id.property);
            expandButton = itemView.findViewById(R.id.expandButton);
            expandedRecyclerView = itemView.findViewById(R.id.expandedRecyclerView);
        }
    }
}
