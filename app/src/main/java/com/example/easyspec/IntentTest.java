package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Data.Users;
import com.example.easyspec.EachProductPage.EachProductPage;
import com.example.easyspec.Firebase.FirebaseHelper;
import com.example.easyspec.databinding.ActivityIntentTestBinding;

public class IntentTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityIntentTestBinding binding = ActivityIntentTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.intentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // FirebaseHelper를 사용하여 데이터 로드
                FirebaseHelper.getInstance().fetchAllDataFromFirebase(new FirebaseHelper.FirebaseCallback() {
                    @Override
                    public void onSuccess(java.util.List<ProductItem> productItems) {
                        if (!productItems.isEmpty()) {
                            // 임의의 ProductItem 선택 (첫 번째 아이템)
                            ProductItem selectedProduct = productItems.get(0);

                            // Intent를 생성하고 ProductItem을 전달
                            Intent intent = new Intent(IntentTest.this, EachProductPage.class);
                            intent.putExtra("productName", selectedProduct.getName());
                            startActivity(intent);
                        } else {
                            Toast.makeText(IntentTest.this, "No products available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // 데이터 로드 실패 처리
                        e.printStackTrace();
                        Toast.makeText(IntentTest.this, "Failed to load product data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // FirebaseHelper를 사용하여 유저 데이터를 로드
                FirebaseHelper.getInstance().fetchAllUsersFromFirebase(new FirebaseHelper.FirebaseUserCallback() {
                    @Override
                    public void onSuccess(java.util.List<Users> users) {
                        if (!users.isEmpty()) {
                            // 임의의 유저 선택 (첫 번째 유저)
                            Users selectedUser = users.get(0);

                            // Intent 생성 및 유저 데이터 전달
                            Intent intent = new Intent(IntentTest.this, InventoryProductPage.class);
                            intent.putExtra("email", selectedUser.getEmail());
                            intent.putExtra("university", selectedUser.getUniversity());
                            intent.putExtra("laptop", selectedUser.getLaptop());
                            intent.putExtra("tablet", selectedUser.getTablet());
                            intent.putExtra("phone", selectedUser.getPhone());

                            // InventoryProductPage 시작
                            startActivity(intent);
                        } else {
                            Toast.makeText(IntentTest.this, "No user data found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // 데이터 로드 실패 처리
                        e.printStackTrace();
                        Toast.makeText(IntentTest.this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
