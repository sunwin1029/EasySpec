package com.example.easyspec;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Data.Users;
import com.example.easyspec.EachProductPage.EachProductPage;
import com.example.easyspec.Firebase.FirebaseHelper;
import com.example.easyspec.databinding.ActivityInventoryProductPageBinding;
import com.example.easyspec.databinding.InventoryProductPageLayoutBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryProductPage extends AppCompatActivity implements View.OnClickListener {

    private ActivityInventoryProductPageBinding binding;
    private List<ProductItem> productList = new ArrayList<>(); // 전체 데이터
    private List<ProductItem> filteredList = new ArrayList<>(); // 필터링된 데이터
    private InventoryAdapter adapter;
    private FirebaseHelper firebaseHelper;

    private AlertDialog listdialog;
    private int sortType;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // FirebaseHelper 초기화
        firebaseHelper = FirebaseHelper.getInstance();

        // Intent로 전달된 사용자 데이터 가져오기
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        /*
        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

         */

        // UI 클릭 리스너 설정
        binding.searchBar.setOnClickListener(this);
        binding.check.setOnClickListener(this);

        // RecyclerView 설정
        adapter = new InventoryAdapter(filteredList);
        binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productRecyclerView.setAdapter(adapter);

        // Firebase에서 데이터 로드 및 필터링
        fetchProductData(1); // 초기에는 productType = 1로 필터링
    }

    private void fetchProductData(int productType) {
        firebaseHelper.fetchAllDataFromFirebase(new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess(List<ProductItem> productItems) {
                // 전체 데이터 저장
                productList.clear();
                productList.addAll(productItems);

                // 특정 productType 기준으로 필터링
                filteredList.clear();
                filteredList.addAll(
                        productList.stream()
                                .filter(product -> product.getProductType() == productType)
                                .collect(Collectors.toList())
                );

                // RecyclerView 갱신
                adapter.notifyDataSetChanged();

                Log.d("InventoryProductPage", "Data loaded: " + filteredList.size() + " items.");
                Toast.makeText(InventoryProductPage.this, "Loaded " + filteredList.size() + " items.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("InventoryProductPage", "Error loading data", e);
                Toast.makeText(InventoryProductPage.this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == binding.searchBar) {
            Toast.makeText(this, "SearchBar clicked", Toast.LENGTH_SHORT).show();
        } else if (view == binding.check) {
            String[] data = getResources().getStringArray(R.array.depart_name);
            listdialog = new AlertDialog.Builder(this)
                    .setTitle("Select a category")
                    .setSingleChoiceItems(R.array.depart_name, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sortType = i + 1; // sortType 설정 (1, 2, 3...)
                        }
                    })
                    .setPositiveButton("Apply", (dialog, which) -> {
                        // 선택된 sortType으로 데이터 필터링
                        fetchProductData(sortType);
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            listdialog.show();
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
        private final List<ProductItem> list;

        public InventoryAdapter(List<ProductItem> list) {
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
            holder.binding.ratingText.setText(String.valueOf(productItem.getAverageRating()));

            // 즐겨찾기 아이콘 설정
            holder.binding.heartIcon.setImageResource(productItem.isFavorite() ? R.drawable.heart : R.drawable.heart_empty);

            // heart 아이콘 클릭 리스너
            holder.binding.heartIcon.setOnClickListener(view -> {
                if (productItem.isFavorite()) {
                    firebaseHelper.removeFavoriteItem(userId, productItem.getName(), new FirebaseHelper.FirebaseCallback() {
                        @Override
                        public void onSuccess(List<ProductItem> productItems) {
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
                    firebaseHelper.addFavoriteItem(userId, productItem.getName(), new FirebaseHelper.FirebaseCallback() {
                        @Override
                        public void onSuccess(List<ProductItem> productItems) {
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
            });

            // 아이템 클릭 리스너
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(holder.itemView.getContext(), EachProductPage.class);
                intent.putExtra("selectedProduct", productItem);
                holder.itemView.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
