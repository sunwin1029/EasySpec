package com.example.easyspec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final List<Item> items;
    private final OnItemClickListener listener;
    private boolean isDescriptionActive = false; // 설명창이 활성화되어 있는지 여부

    // 클릭 이벤트 인터페이스
    public interface OnItemClickListener {
        void onItemClick(String description);
    }

    // 생성자
    public ItemAdapter(List<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // 설명창이 활성화되었는지 설정
    public void setDescriptionActive(boolean active) {
        isDescriptionActive = active;
        notifyDataSetChanged(); // 상태 변화 후 리사이클러뷰 갱신
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item, listener, isDescriptionActive); // 상태도 함께 전달
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 뷰 홀더 클래스
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView itemImage;
        private final TextView itemTitle;
        private final TextView itemSubtitle;
        private final Button viewDetailsButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemSubtitle = itemView.findViewById(R.id.itemSubtitle);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
        }

        public void bind(final Item item, final OnItemClickListener listener, boolean isDescriptionActive) {
            itemImage.setImageResource(item.getImageResId());
            itemTitle.setText(item.getTitle());
            itemSubtitle.setText(item.getSubtitle());

            // 설명창이 열려있다면 버튼 비활성화
            viewDetailsButton.setEnabled(!isDescriptionActive);

            viewDetailsButton.setOnClickListener(v -> listener.onItemClick(item.getDescription()));
        }
    }
}
