package com.example.easyspec.Firebase;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Firebase.FirebaseHelper;
import com.example.easyspec.ProductAdapter;
import com.example.easyspec.R;

import java.util.ArrayList;
import java.util.List;

public class FirebaseTestActivity extends AppCompatActivity {

    private ListView listViewProducts; // ListView
    private ProductAdapter productAdapter; // 어댑터
    private List<ProductItem> productItems = new ArrayList<>(); // 제품 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test); // 레이아웃 설정

        // ListView 연결
        listViewProducts = findViewById(R.id.listViewProducts);

        // 어댑터 초기화 및 연결
        productAdapter = new ProductAdapter(this, productItems);
        listViewProducts.setAdapter(productAdapter);

        // Firebase에서 데이터 불러오기
        FirebaseHelper.getInstance().fetchAllDataFromFirebase(new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess(List<ProductItem> items) {
                // 데이터 로드 성공 시 리스트 업데이트
                productItems.clear();
                productItems.addAll(items);
                productAdapter.notifyDataSetChanged(); // 어댑터 갱신
            }

            @Override
            public void onFailure(Exception e) {
                // 데이터 로드 실패 시 로그 출력
                Log.e("FirebaseTestActivity", "Error fetching data", e);
            }
        });
    }
}
