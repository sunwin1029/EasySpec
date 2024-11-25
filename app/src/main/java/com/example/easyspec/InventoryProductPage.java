package com.example.easyspec;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.databinding.ActivityInventoryProductPageBinding;
import com.example.easyspec.databinding.InventoryProductPageLayoutBinding;
import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Firebase.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;


public class InventoryProductPage extends AppCompatActivity implements View.OnClickListener{
    ActivityInventoryProductPageBinding binding;
    List<ProductItem> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.searchBar.setOnClickListener(this);
        binding.check.setOnClickListener(this);
        binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productRecyclerView.setAdapter(new InventoryAdapter(list));
        fetchProductData();
    }

    private void fetchProductData() {
        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(); // FirebaseHelper의 싱글톤 인스턴스 가져오기

        // 데이터를 불러오는 메서드 호출
        firebaseHelper.fetchDataFromFirebase(new FirebaseHelper.FirebaseCallback() {
            @Override
            public void onSuccess(List<ProductItem> productItems) {
                // 데이터 로드 성공
                list.clear(); // 기존 데이터 초기화
                list.addAll(productItems); // 불러온 데이터를 리스트에 추가

                // 데이터를 성공적으로 로드했음을 UI나 로그로 확인
                Log.d("Firebase", "Data loaded successfully: " + list.size() + " items.");
                Toast.makeText(InventoryProductPage.this, "Data loaded: " + list.size() + " items.", Toast.LENGTH_SHORT).show();

                // TODO: 여기서 데이터를 RecyclerView 등 UI 요소에 적용
                //updateUIWithData();
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
        if(view == binding.searchBar) {
            Toast.makeText(this, "searchBar clicked", Toast.LENGTH_SHORT).show();
        }
        else if(view == binding.check) {
            Toast.makeText(this, "checkButton clicked", Toast.LENGTH_SHORT).show();
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
            holder.binding.productName.setText(productItem.getName());
            holder.binding.productPrice.setText(productItem.getPrice());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }



}