package com.example.easyspec;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.Data.ProductItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        // 제품 가격 설정
        holder.productPrice.setText(String.format("₩%d", productItem.getPrice())); // 가격을 문자열로 변환하여 설정

        // 제품 이미지 설정
        loadProductImage(productItem.getId(), holder.productImage);

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductSelected(productItem);
            }
        });

        // 즐겨찾기 버튼 클릭 리스너 설정
        holder.btnFavorite.setImageResource(R.drawable.heart); // 기본적으로 노란색 별로 설정
        holder.btnFavorite.setOnClickListener(v -> {
            // 즐겨찾기에서 삭제
            removeFromFavorites(productItem.getId());
            favoritesList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView productImage;
        ImageButton btnFavorite;
        TextView productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productImage = itemView.findViewById(R.id.product_image); // XML에서 제품 이미지 뷰 초기화
            btnFavorite = itemView.findViewById(R.id.btn_favorite); // 즐겨찾기 버튼 초기화
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }

    private void loadProductImage(String productId, ImageView productImage) {
        String imageName = productId.toLowerCase(); // product1, product2 등
        int imageResId = productImage.getContext().getResources().getIdentifier(
                imageName, "drawable", productImage.getContext().getPackageName()
        );

        Log.d("ProductImage", "ProductId: " + productId + ", ImageName: " + imageName + ", ResId: " + imageResId);

        if (imageResId != 0) {
            productImage.setImageResource(imageResId);
        } else {
            productImage.setImageResource(R.drawable.iphone15_promax);
        }
    }

    private void removeFromFavorites(String productId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 현재 로그인한 사용자 ID
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("favorites");
        favoritesRef.child(productId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FavoritesAdapter", "Product removed from favorites: " + productId);
            } else {
                Log.e("FavoritesAdapter", "Failed to remove product from favorites: " + task.getException().getMessage());
            }
        });
    }
}
