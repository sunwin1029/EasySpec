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
import com.example.easyspec.EachProductPage.EachProductPage;
import com.example.easyspec.databinding.ActivityInventoryProductPageBinding;
import com.example.easyspec.databinding.InventoryProductPageLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InventoryProductPage extends AppCompatActivity implements View.OnClickListener {

    private ActivityInventoryProductPageBinding binding;
    private List<ProductItem> productList = new ArrayList<>(); // 전체 데이터
    private List<ProductItem> filteredList = new ArrayList<>(); // 필터링된 데이터
    private InventoryAdapter adapter;

    private DatabaseReference databaseReference; // Firebase 참조
    private DatabaseReference userReference; // Firebase Users 노드 참조
    private AlertDialog listdialog;

    private String userId; // 사용자 ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase 참조 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("ProductItems");
        userReference = FirebaseDatabase.getInstance().getReference("Users");

        // Intent로 전달된 사용자 ID 가져오기
        userId = getIntent().getStringExtra("userId");

        if (userId == null || userId.isEmpty()) {
            // 사용자 정보가 없을 경우, Firebase에서 기본 사용자 로드
            fetchDefaultUserFromFirebase();
        } else {
            // 사용자 정보가 있을 경우 메시지 표시
            Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
        }

        // RecyclerView 초기화
        adapter = new InventoryAdapter(filteredList);
        binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productRecyclerView.setAdapter(adapter);

        // Firebase에서 모든 데이터 로드
        fetchProductData();

        // UI 클릭 리스너 설정
        binding.searchBar.setOnClickListener(this);
        binding.check.setOnClickListener(this);
    }

    private void fetchDefaultUserFromFirebase() {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // 첫 번째 사용자 ID를 임의로 가져오기
                    DataSnapshot firstUser = snapshot.getChildren().iterator().next();
                    userId = firstUser.getKey(); // 사용자 ID
                    String email = firstUser.child("email").getValue(String.class);

                    Toast.makeText(InventoryProductPage.this,
                            "Default User ID: " + userId + "\nEmail: " + email,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("InventoryProductPage", "No users found in the database.");
                    Toast.makeText(InventoryProductPage.this,
                            "No users found in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("InventoryProductPage", "Failed to load user data", error.toException());
                Toast.makeText(InventoryProductPage.this,
                        "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();

                // ProductItems 노드의 모든 데이터를 순회하며 ProductItem 객체 생성
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    ProductItem productItem = createProductItemFromSnapshot(productSnapshot);
                    productList.add(productItem);
                }

                // RecyclerView 갱신
                filteredList.clear();
                filteredList.addAll(productList);
                adapter.notifyDataSetChanged();

                Log.d("InventoryProductPage", "Loaded products: " + filteredList.size());
                Toast.makeText(InventoryProductPage.this, "Loaded " + filteredList.size() + " items.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("InventoryProductPage", "Failed to load data", error.toException());
                Toast.makeText(InventoryProductPage.this, "Failed to load data.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private ProductItem createProductItemFromSnapshot(DataSnapshot snapshot) {
        String name = snapshot.child("name").getValue(String.class);
        int price = snapshot.child("price").getValue(Integer.class);
        String company = snapshot.child("Company").getValue(String.class);
        int productType = snapshot.child("productType").getValue(Integer.class);
        double totalRating = snapshot.child("rating/totalRating").getValue(Double.class) != null ?
                snapshot.child("rating/totalRating").getValue(Double.class) : 0.0;
        int ratingCount = snapshot.child("rating/ratingCount").getValue(Integer.class) != null ?
                snapshot.child("rating/ratingCount").getValue(Integer.class) : 0;

        // 나머지 필드는 null 또는 기본 값으로 초기화
        return new ProductItem(
                name, price, null, productType, company, "Features",
                totalRating, ratingCount, false, null, 0, 0, 0, 0, 0, 0
        );
    }

    @Override
    public void onClick(View view) {
        if (view == binding.searchBar) {
            Toast.makeText(this, "SearchBar clicked", Toast.LENGTH_SHORT).show();
        } else if (view == binding.check) {
            String[] data = getResources().getStringArray(R.array.depart_name);
            listdialog = new AlertDialog.Builder(this)
                    .setTitle("Select a category")
                    .setSingleChoiceItems(R.array.depart_name, -1, (dialogInterface, i) -> {
                        // 이후 필터링 기준 추가 가능
                    })
                    .setPositiveButton("Apply", (dialog, which) -> {
                        // 이후 필터링 로직 추가 가능
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
            String productName = productItem.getName();

            DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("favorites");

            // 즐겨찾기 상태를 저장하는 boolean 변수
            final boolean[] isFavorite = {false};

            userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // `DataSnapshot`을 순회하며 `favorites` 확인
                    for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                        String favoriteName = favoriteSnapshot.getValue(String.class); // 값 가져오기
                        if (productName.equals(favoriteName)) {
                            isFavorite[0] = true; // 즐겨찾기 상태 업데이트
                            break;
                        }
                    }

                    // 아이콘 상태 설정
                    holder.binding.heartIcon.setImageResource(
                            isFavorite[0] ? R.drawable.heart : R.drawable.heart_empty
                    );

                    // 클릭 이벤트 처리
                    holder.binding.heartIcon.setOnClickListener(view -> {
                        if (isFavorite[0]) {
                            // 즐겨찾기에서 제거
                            userFavoritesRef.orderByValue().equalTo(productName)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                                item.getRef().removeValue(); // Firebase에서 제거
                                            }
                                            Toast.makeText(holder.itemView.getContext(),
                                                    productName + " removed from favorites.",
                                                    Toast.LENGTH_SHORT).show();
                                            holder.binding.heartIcon.setImageResource(R.drawable.heart_empty);
                                            isFavorite[0] = false; // 상태 업데이트
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("InventoryAdapter", "Failed to remove favorite", error.toException());
                                        }
                                    });
                        } else {
                            // 즐겨찾기에 추가
                            userFavoritesRef.push().setValue(productName).addOnSuccessListener(aVoid -> {
                                Toast.makeText(holder.itemView.getContext(),
                                        productName + " added to favorites.",
                                        Toast.LENGTH_SHORT).show();
                                holder.binding.heartIcon.setImageResource(R.drawable.heart);
                                isFavorite[0] = true; // 상태 업데이트
                            }).addOnFailureListener(e -> {
                                Log.e("InventoryAdapter", "Failed to add favorite", e);
                            });
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("InventoryAdapter", "Failed to load favorites", error.toException());
                }
            });

            // 제품 정보 설정
            holder.binding.productName.setText(productItem.getName());
            holder.binding.productPrice.setText(String.format("₩%,d", productItem.getPrice()));
            holder.binding.ratingText.setText(String.format("%.1f", productItem.getAverageRating()));

            // 제품 상세 보기로 이동
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(holder.itemView.getContext(), EachProductPage.class);
                intent.putExtra("selectedProduct", productItem);
                intent.putExtra("userId", userId);
                holder.itemView.getContext().startActivity(intent);
            });
        }




        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
