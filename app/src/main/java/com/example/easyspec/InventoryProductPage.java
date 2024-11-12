package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
        binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productRecyclerView.setAdapter(new ProductAdapter(list)); // 아직 구현 더해야함
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {
        EachProductLayoutBinding binding;

        public ProductViewHolder(EachProductLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

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


            // 뷰홀더에 담을 내용들
            /*
            holder.binding.productImage.setImageResource(리소스);
            holder.binding.productPrice.setText(가격);
            holder.binding.productName.setText(상품 이름);
            holder.binding.ratingText.setText(평점);
            holder.binding.heartIcon.

             */
        }


        @Override
        public int getItemCount() {
            return 0;
        }
    }
}


