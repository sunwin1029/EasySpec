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

    private static final String TAG = "FavoriteProductsActivity";

    private RecyclerView favoritesRecyclerView;
    private DatabaseReference usersRef;
    private String userId; // 현재 로그인한 사용자 ID
    private List<ProductItem> favoriteProducts = new ArrayList<>(); // 제품 목록

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
            Toast.makeText(this, "로그인 상태가 아닙니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        fetchFavoritesFromFirebase();
    }

    private void fetchFavoritesFromFirebase() {
        usersRef.child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(FavoritesActivity.this, "즐겨찾기 제품이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 즐겨찾기 제품 ID 및 이름을 가져오기
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
                                    favoriteProducts.add(productItem);
                                    setupRecyclerView(); // RecyclerView 업데이트
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "제품 데이터 로드 실패: " + error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "데이터 로드 실패: " + error.getMessage());
            }
        });
    }


    private void setupRecyclerView() {
        FavoritesAdapter adapter = new FavoritesAdapter(favoriteProducts, this, this);
        favoritesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onProductSelected(ProductItem productItem) {
        showProductDetails(productItem.getId());
    }

    private void showProductDetails(String productId) {
        Intent intent = new Intent(this, EachProductPage.class);
        intent.putExtra("productId", productId);
        intent.putExtra("userId", userId); // 사용자 ID도 전달
        startActivity(intent);
    }
}
