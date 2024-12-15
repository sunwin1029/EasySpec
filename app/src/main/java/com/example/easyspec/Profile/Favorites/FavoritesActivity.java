package com.example.easyspec.Profile.Favorites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.EachProductPage.EachProductPage;
import com.example.easyspec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements FavoritesAdapter.OnProductClickListener {

    private static final String TAG = "EasySpec"; // 로그 태그 변경

    private RecyclerView favoritesRecyclerView;
    private DatabaseReference usersRef;
    private String userId; // 현재 로그인한 사용자 ID
    private List<ProductItem> favoriteProducts = new ArrayList<>(); // 즐겨찾기 제품 목록

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 수직 레이아웃 설정

        // Firebase Authentication을 통해 현재 사용자 ID 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid(); // 현재 로그인한 사용자 ID
        } else {
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show();
            finish(); // 로그인하지 않은 경우 액티비티 종료
            return;
        }

        usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        fetchFavoritesFromFirebase(); // 즐겨찾기 데이터 불러오기
    }

    private void fetchFavoritesFromFirebase() {
        usersRef.child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(FavoritesActivity.this, "There are no favorite products.", Toast.LENGTH_SHORT).show();
                    return; // 즐겨찾기 제품이 없는 경우
                }

                // 즐겨찾기 제품 ID를 가져오기
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String productId = productSnapshot.getKey();
                    if (productId != null) {
                        // productId로 ProductItems에서 제품 정보를 가져오기
                        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("ProductItems").child(productId);
                        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                                String productName = productSnapshot.child("name").getValue(String.class);
                                Integer productPrice = productSnapshot.child("price").getValue(Integer.class); // 가격 정보 가져오기

                                if (productName != null && productPrice != null) {
                                    // 제품 정보 생성
                                    ProductItem productItem = new ProductItem(productName, productPrice, null, 0, "", "", 0, 0, false, null, 0, 0, 0, 0, 0, 0);
                                    productItem.setId(productId); // 제품 ID 설정
                                    favoriteProducts.add(productItem); // 즐겨찾기 목록에 추가
                                    setupRecyclerView(); // RecyclerView 업데이트
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Failed to load product data: " + error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load data: " + error.getMessage());
            }
        });
    }

    private void setupRecyclerView() {
        FavoritesAdapter adapter = new FavoritesAdapter(favoriteProducts, this, this);
        favoritesRecyclerView.setAdapter(adapter); // RecyclerView 어댑터 설정
    }

    @Override
    public void onProductSelected(ProductItem productItem) {
        showProductDetails(productItem.getId()); // 제품 상세 페이지로 이동
    }

    private void showProductDetails(String productId) {
        Intent intent = new Intent(this, EachProductPage.class);
        intent.putExtra("productId", productId); // 제품 ID 전달
        intent.putExtra("userId", userId); // 사용자 ID도 전달
        startActivity(intent); // 상세 페이지 시작
    }
}
