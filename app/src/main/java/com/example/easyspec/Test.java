package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.EachProductPage.EachProductPage;
import com.example.easyspec.Firebase.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class Test extends AppCompatActivity {

    private Button button;
    private List<ProductItem> productItemList = new ArrayList<>(); // 데이터를 저장할 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        button = findViewById(R.id.button);

        // 버튼 클릭 리스너
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // FirebaseHelper를 사용해 데이터 가져오기
                fetchProductItems();
            }
        });
    }

    /**
     * Firebase에서 ProductItem 데이터를 가져오는 메서드
     */
    private void fetchProductItems() {
        FirebaseHelper.getInstance().fetchAllDataFromFirebase(new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess(List<ProductItem> productItems) {
                // 성공적으로 데이터를 가져왔을 때
                productItemList.clear(); // 기존 데이터를 초기화
                productItemList.addAll(productItems); // 가져온 데이터를 리스트에 추가

                // 첫 번째 ProductItem을 추출하여 Intent로 넘김
                if (!productItemList.isEmpty()) {
                    ProductItem firstProduct = productItemList.get(0);

                    // Intent 생성 및 객체 전달
                    Intent intent = new Intent(Test.this, EachProductPage.class);
                    intent.putExtra("selectedProduct", firstProduct); // 객체 자체 전달
                    startActivity(intent);
                } else {
                    // 리스트가 비어있는 경우
                    Toast.makeText(Test.this, "No products available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                // 데이터 가져오기에 실패했을 때
                Toast.makeText(Test.this, "Failed to load product items.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}