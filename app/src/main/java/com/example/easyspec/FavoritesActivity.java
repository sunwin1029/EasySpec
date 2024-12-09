package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.EachProductPage.EachProductPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private static final String TAG = "FavoriteProductsActivity";

    private ListView favoritesListView;
    private DatabaseReference usersRef;
    private String userId; // 현재 로그인한 사용자 ID
    private List<String> favoriteProductIds = new ArrayList<>();
    private List<String> favoriteProductNames = new ArrayList<>(); // 제품 이름 목록

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesListView = findViewById(R.id.favoritesListView);

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
                    String productName = productSnapshot.getValue(String.class);
                    if (productId != null && productName != null) {
                        favoriteProductIds.add(productId);
                        favoriteProductNames.add(productName);
                    }
                }

                setupListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "데이터 로드 실패: " + error.getMessage());
            }
        });
    }


    private void setupListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoriteProductNames);
        favoritesListView.setAdapter(adapter);
        favoritesListView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            String selectedProductId = favoriteProductIds.get(position);
            showProductDetails(selectedProductId);
        });
    }

    private void showProductDetails(String productId) {
        Intent intent = new Intent(this, EachProductPage.class);
        intent.putExtra("productId", productId);
        intent.putExtra("userId", userId); // 사용자 ID도 전달
        startActivity(intent);
    }
}
