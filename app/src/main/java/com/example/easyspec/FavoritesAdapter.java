package com.example.easyspec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.Data.ProductItem;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private List<ProductItem> favoritesList;
    private Context context;
    private OnProductClickListener onProductClickListener;

    public interface OnProductClickListener {
        void onProductSelected(ProductItem productItem);
    }

    public FavoritesAdapter(List<ProductItem> favoritesList, Context context, OnProductClickListener listener) {
        this.favoritesList = favoritesList;
        this.context = context;
        this.onProductClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductItem productItem = favoritesList.get(position);
        holder.productName.setText(productItem.getName());

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductSelected(productItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.favorite_product_name);
        }
    }
}
