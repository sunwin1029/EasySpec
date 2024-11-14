package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.databinding.ActivityInventoryProductPageBinding;
import com.example.easyspec.databinding.EachProductLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class InventoryProductPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityInventoryProductPageBinding binding = ActivityInventoryProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<ProductLayoutItem> list = new ArrayList<>();
        // 여기서부터 상품 목록에 넣을 목록 추가하면 됨!
        list.add(new ProductLayoutItem("Iphone 15 pro", 1500000, R.drawable.iphone15_promax, 4.4f, false));


        binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productRecyclerView.setAdapter(new ProductAdapter(list));
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        EachProductLayoutBinding binding;

        public ProductViewHolder(EachProductLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

        private List<ProductLayoutItem> list;

        public ProductAdapter(List<ProductLayoutItem> list) {
            this.list = list;
        }


        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            EachProductLayoutBinding binding = EachProductLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ProductViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {


            ProductLayoutItem item = list.get(position);

            // 뷰홀더에 담을 내용들

            holder.binding.productImage.setImageResource(item.getImageResource());
            holder.binding.productPrice.setText(String.valueOf(item.getPrice()));
            holder.binding.productName.setText(String.valueOf(item.getName()));
            holder.binding.ratingText.setText(String.valueOf(item.getRating()));
            if(item.heart()) {
                holder.binding.heartIcon.setImageResource(R.drawable.heart);
            } else {
                holder.binding.heartIcon.setImageResource(R.drawable.heart_empty);
            }

        }


        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}


