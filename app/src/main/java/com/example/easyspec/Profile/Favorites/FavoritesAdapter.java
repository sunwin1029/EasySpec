package com.example.easyspec.Profile.Favorites;

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
import com.example.easyspec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private List<ProductItem> favoritesList; // 즐겨찾기 제품 목록
    private Context context; // 컨텍스트
    private OnProductClickListener onProductClickListener; // 제품 클릭 리스너

    public interface OnProductClickListener {
        void onProductSelected(ProductItem productItem); // 제품 선택 이벤트
    }

    public FavoritesAdapter(List<ProductItem> favoritesList, Context context, OnProductClickListener listener) {
        this.favoritesList = favoritesList;
        this.context = context;
        this.onProductClickListener = listener; // 클릭 리스너 설정
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false); // 즐겨찾기 아이템 레이아웃 인플레이트
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductItem productItem = favoritesList.get(position); // 현재 제품 아이템
        holder.productName.setText(productItem.getName()); // 제품 이름 설정

        // 제품 가격 설정
        holder.productPrice.setText(String.format("₩%d", productItem.getPrice())); // 가격을 문자열로 변환하여 설정

        // 제품 이미지 설정
        loadProductImage(productItem.getId(), holder.productImage);

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductSelected(productItem); // 제품 선택 시 콜백 호출
            }
        });

        // 즐겨찾기 버튼 클릭 리스너 설정
        holder.btnFavorite.setImageResource(R.drawable.heart); // 기본적으로 즐겨찾기 버튼 이미지를 설정
        holder.btnFavorite.setOnClickListener(v -> {
            // 즐겨찾기에서 삭제
            removeFromFavorites(productItem.getId()); // Firebase에서 제품 제거
            favoritesList.remove(position); // 목록에서 제품 제거
            notifyItemRemoved(position); // RecyclerView 업데이트
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size(); // 즐겨찾기 아이템 수 반환
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName; // 제품 이름
        ImageView productImage; // 제품 이미지
        ImageButton btnFavorite; // 즐겨찾기 버튼
        TextView productPrice; // 제품 가격

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name); // XML에서 제품 이름 뷰 초기화
            productImage = itemView.findViewById(R.id.product_image); // XML에서 제품 이미지 뷰 초기화
            btnFavorite = itemView.findViewById(R.id.btn_favorite); // XML에서 즐겨찾기 버튼 초기화
            productPrice = itemView.findViewById(R.id.product_price); // XML에서 제품 가격 뷰 초기화
        }
    }

    private void loadProductImage(String productId, ImageView productImage) {
        String imageName = productId.toLowerCase(); // product1, product2 등으로 변환
        int imageResId = productImage.getContext().getResources().getIdentifier(
                imageName, "drawable", productImage.getContext().getPackageName()
        );

        // 이미지 로드 로그
        Log.d("EasySpec", "ProductId: " + productId + ", ImageName: " + imageName + ", ResId: " + imageResId);

        if (imageResId != 0) {
            productImage.setImageResource(imageResId); // 이미지 설정
        } else {
            productImage.setImageResource(R.drawable.iphone15_promax); // 기본 이미지 설정
        }
    }

    private void removeFromFavorites(String productId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 현재 로그인한 사용자 ID
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("favorites");
        favoritesRef.child(productId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("EasySpec", "Product removed from favorites: " + productId); // Product removed successfully log
            } else {
                Log.e("EasySpec", "Failed to remove product from favorites: " + task.getException().getMessage()); // Product removal failed log
            }
        });
    }
}
