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

import com.example.easyspec.Data.Users;
import com.example.easyspec.EachProductPage.EachProductPage;
import com.example.easyspec.databinding.ActivityInventoryProductPageBinding;
import com.example.easyspec.databinding.InventoryProductPageLayoutBinding;
import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Firebase.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class InventoryProductPage extends AppCompatActivity implements View.OnClickListener {
    ActivityInventoryProductPageBinding binding;
    List<ProductItem> list = new ArrayList<>();

    // FirebaseHelper 인스턴스
    FirebaseHelper firebaseHelper;


    public AlertDialog listdialog;
    int sortType;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // FirebaseHelper 초기화
        firebaseHelper = FirebaseHelper.getInstance();

        // Intent로 전달된 사용자 데이터 가져오기
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId"); // 전달받은 userId 사용
        /*
        // userId가 null이면 FirebaseAuth에서 가져오기
        if (userId == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            } else {
                Toast.makeText(this, "User not logged in. Redirecting to login screen.", Toast.LENGTH_SHORT).show();
                // 로그인 화면으로 리디렉션
                Intent loginIntent = new Intent(this, LoginActivity.class); // LoginActivity를 실제 구현해야 합니다.
                startActivity(loginIntent);
                finish();
                return;
            }
        }

         */

        // UI 클릭 리스너 설정
        binding.searchBar.setOnClickListener(this);
        binding.check.setOnClickListener(this);

        // RecyclerView 설정
        binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productRecyclerView.setAdapter(new InventoryAdapter(list));

        // Firebase에서 데이터 로드
        fetchProductData();

        Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
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
            String[] data = getResources().getStringArray(R.array.depart_name);
            listdialog = new AlertDialog.Builder(this)
                    .setTitle("대학을 골라주세요")
                    .setSingleChoiceItems(R.array.depart_name, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sortType = i;
                        }
                    })
                    .setPositiveButton("확인", null)
                    .setNegativeButton("확인", null)
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
            holder.binding.heartIcon.setOnClickListener(view -> {
                if (productItem.isFavorite()) {
                    // 즐겨찾기에서 제거
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
                    // 즐겨찾기에 추가
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

            // 아이템 전체 클릭 리스너
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(holder.itemView.getContext(), EachProductPage.class);

                // ProductItem 객체를 Intent로 전달
                intent.putExtra("selectedProduct", productItem);

                holder.itemView.getContext().startActivity(intent);

                // 로그로 확인
                Log.d("InventoryProductPage", "ProductItem sent: " + productItem.getName());
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