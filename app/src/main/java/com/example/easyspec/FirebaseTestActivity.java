package com.example.easyspec;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class FirebaseTestActivity extends AppCompatActivity {

    private TextView textViewProducts;
    private Button buttonUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);

        textViewProducts = findViewById(R.id.textViewProducts);
        buttonUpdate = findViewById(R.id.buttonUpdate);

        // 데이터 로드
        FirebaseHelper.getInstance().fetchDataFromFirebase(new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess(List<ProductItem> productItems) {
                StringBuilder productList = new StringBuilder();
                for (ProductItem item : productItems) {
                    productList.append("Name: ").append(item.getName())
                            .append(", Price: ").append(item.getPrice())
                            .append(", Type: ").append(item.getProductType())
                            .append("\n");
                }
                textViewProducts.setText(productList.toString());
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FirebaseTestActivity", "Error fetching data", e);
                textViewProducts.setText("Error fetching data: " + e.getMessage());
            }
        });

        // 버튼 클릭 시 제품 수정
        buttonUpdate.setOnClickListener(v -> {
            ProductItem updatedItem = new ProductItem(
                    "수정된 제품 이름",  // 수정된 이름
                    1500000,           // 수정된 가격
                    0,                 // 이미지 리소스
                    4.5,               // 수정된 평점
                    true,              // 수정된 즐겨찾기 상태
                    1,                 // 제품 타입 (노트북)
                    null               // UsersOfTheProduct
            );

            FirebaseHelper.getInstance().updateProduct(
                    "laptops", // 카테고리
                    "product3zz", // 제품 ID
                    updatedItem,
                    new FirebaseHelper.FirebaseCallback() {
                        @Override
                        public void onSuccess(List<ProductItem> productItems) {
                            Log.d("FirebaseTestActivity", "Product updated successfully!");
                            textViewProducts.setText("Product updated successfully!");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("FirebaseTestActivity", "Failed to update product", e);
                            textViewProducts.setText("Failed to update product: " + e.getMessage());
                        }
                    }
            );
        });
    }
}
