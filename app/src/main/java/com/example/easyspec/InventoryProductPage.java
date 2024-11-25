package com.example.easyspec;

import android.app.Activity;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        List<ProductItem> list = firebaseHelper.getProductItems();


        binding.searchBar.setOnClickListener(this);
        binding.check.setOnClickListener(this);
        binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productRecyclerView.setAdapter(new InventoryAdapter(list));
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
            // 설정사항
            /*

             */
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }



}