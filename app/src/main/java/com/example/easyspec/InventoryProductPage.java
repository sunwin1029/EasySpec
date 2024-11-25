package com.example.easyspec;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.databinding.ActivityInventoryProductPageBinding;
import com.example.easyspec.databinding.InventoryProductPageLayoutBinding;
import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Firebase.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class InventoryProductPage extends AppCompatActivity implements View.OnClickListener {
    ActivityInventoryProductPageBinding binding;
    List<ProductItem> list = new ArrayList<>();

    // FirebaseHelper 인스턴스
    FirebaseHelper firebaseHelper;

    // 사용자 ID (Intent로 전달받아 초기화)
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // FirebaseHelper 초기화
        firebaseHelper = FirebaseHelper.getInstance();

        // Intent로 전달된 userId를 가져옴
        userId = getIntent().getStringExtra("userId");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID is missing. Please log in again.", Toast.LENGTH_SHORT).show();
            finish(); // userId가 없으면 액티비티 종료
            return;
        }

        // UI 클릭 리스너 설정
        binding.searchBar.setOnClickListener(this);
        binding.check.setOnClickListener(this);

        // RecyclerView 설정
        binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productRecyclerView.setAdapter(new InventoryAdapter(list));

        // Firebase에서 데이터 로드
        fetchProductData();
    }

    private void fetchProductData() {
        // FirebaseHelper를 통해 데이터 로드
        firebaseHelper.fetchAllDataFromFirebase(new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess(List<ProductItem> productItems) {
                // 데이터 로드 성공
                list.clear(); // 기존 데이터를 지워 새로 로드
                list.addAll(productItems); // 불러온 데이터를 리스트에 추가

                // RecyclerView 어댑터 갱신
                binding.productRecyclerView.getAdapter().notifyDataSetChanged();

                Log.d("Firebase", "Data loaded successfully: " + list.size() + " items.");
                Toast.makeText(InventoryProductPage.this, "Data loaded: " + list.size() + " items.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                // 데이터 로드 실패
                Log.e("Firebase", "Error loading data", e);
                Toast.makeText(InventoryProductPage.this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == binding.searchBar) {
            Toast.makeText(this, "SearchBar clicked", Toast.LENGTH_SHORT).show();
        } else if (view == binding.check) {
            Toast.makeText(this, "CheckButton clicked", Toast.LENGTH_SHORT).show();
        }
    }

    private class InventoryViewHolder extends RecyclerView.ViewHolder {
        private InventoryProductPageLayoutBinding binding;

        public InventoryViewHolder(InventoryProductPageLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private class InventoryAdapter extends RecyclerView.Adapter<InventoryViewHolder> {
        List<ProductItem> list;

        private InventoryAdapter(List<ProductItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            InventoryProductPageLayoutBinding binding = InventoryProductPageLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new InventoryViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
            ProductItem productItem = list.get(position);

            // 제품 정보를 UI에 설정
            holder.binding.productName.setText(productItem.getName());
            holder.binding.productPrice.setText(String.valueOf(productItem.getPrice()));
            holder.binding.ratingText.setText(String.valueOf(productItem.getRating()));

            // heart 아이콘 클릭 리스너
            holder.binding.heartIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (productItem.isFavorite()) {
                        // 즐겨찾기에서 제거
                        firebaseHelper.removeFavoriteItem(userId, productItem.getName(), new FirebaseHelper.FirebaseCallback() {
                            @Override
                            public void onSuccess(List<ProductItem> productItems) {
                                // UI 업데이트
                                productItem.setFavorite(false);
                                holder.binding.heartIcon.setImageResource(R.drawable.heart_empty);
                                Toast.makeText(holder.itemView.getContext(), productItem.getName() + " removed from favorites.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(holder.itemView.getContext(), "Failed to remove favorite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // 즐겨찾기에 추가
                        firebaseHelper.addFavoriteItem(userId, productItem.getName(), new FirebaseHelper.FirebaseCallback() {
                            @Override
                            public void onSuccess(List<ProductItem> productItems) {
                                // UI 업데이트
                                productItem.setFavorite(true);
                                holder.binding.heartIcon.setImageResource(R.drawable.heart);
                                Toast.makeText(holder.itemView.getContext(), productItem.getName() + " added to favorites.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(holder.itemView.getContext(), "Failed to add favorite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            // 즐겨찾기 상태에 따라 heart 아이콘 설정
            boolean isFavorite = productItem.isFavorite();
            holder.binding.heartIcon.setImageResource(isFavorite ? R.drawable.heart : R.drawable.heart_empty);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}