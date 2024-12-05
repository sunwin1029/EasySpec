package com.example.easyspec.EachProductPage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.R;
import com.example.easyspec.Review.RatingReviewFragment;
import com.example.easyspec.databinding.ActivityEachProductPageBinding;
import com.example.easyspec.databinding.EachProductPropertyBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EachProductPage extends AppCompatActivity {

    private static final String TAG = "EachProductPage";

    private DatabaseReference databaseReference; // Firebase 참조
    private ProductItem productItem; // 로드된 ProductItem 객체
    private List<String> featureList = new ArrayList<>(); // 리뷰 카테고리 리스트
    private ActivityEachProductPageBinding binding;
    private String userId;

    private ValueEventListener productListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEachProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String productId = getIntent().getStringExtra("productId");
        userId = getIntent().getStringExtra("userId"); // 로컬 변수가 아닌 멤버 변수에 저장

        if (productId == null || userId == null) {
            Toast.makeText(this, "Missing essential data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchProductItemFromFirebase(productId);
    }

    private void setupButtonActions(String productId) {
        binding.EvaluationInEachProduct.setOnClickListener(v -> {
            RatingReviewFragment fragment = new RatingReviewFragment();
            Bundle args = new Bundle();
            args.putString("userId", userId); // 사용자 ID 전달
            args.putString("productId", productId); // 고유 ID 전달
            fragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void fetchProductItemFromFirebase(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance()
                .getReference("ProductItems")
                .child(productId);

        // 리스너 생성
        productListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(EachProductPage.this, "제품 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                productItem = createProductItemFromSnapshot(snapshot);
                updateUIWithProductItem(productItem);

                // 평가 버튼 설정 호출
                setupButtonActions(productId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EachProductPage.this, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        };

        // 실시간 업데이트 감지
        productRef.addValueEventListener(productListener);
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

        ProductItem productItem = new ProductItem(
                name, price, null, productType, company, "Features",
                totalRating, ratingCount, false, null, 0, 0, 0, 0, 0, 0
        );

        // productId 설정
        productItem.setId(snapshot.getKey());
        return productItem;
    }

    private void updateUIWithProductItem(ProductItem item) {
        if (item == null) return;

        binding.NameInEachProduct.setText(item.getName());
        binding.PriceInEachProduct.setText(String.format("₩%,d", item.getPrice()));
        binding.ratingInEachProduct.setText(String.format("%.1f", item.getAverageRating()));
        binding.ImageInEachProduct.setImageResource(item.getImageResource() != null ?
                item.getImageResource() : R.drawable.iphone15_promax);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productListener != null) {
            DatabaseReference productRef = FirebaseDatabase.getInstance()
                    .getReference("ProductItems")
                    .child(productItem.getId());
            productRef.removeEventListener(productListener);
        }
    }
}
