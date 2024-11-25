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

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.databinding.ActivityEachProductPageBinding;
import com.example.easyspec.databinding.EachProductPropertyBinding;

import java.util.ArrayList;
import java.util.List;

public class EachProductPage extends AppCompatActivity {

    ProductItem productItem;
    List<String> featureList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEachProductPageBinding binding = ActivityEachProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("productName");
        int price = getIntent().getIntExtra("productPrice", 0);
        float rating = getIntent().getFloatExtra("productRating", 0.0f);
        int productType = getIntent().getIntExtra("productType", 0 );

        //binding.ImageInEachProduct.setImageResource();
        binding.NameInEachProduct.setText(name);
        binding.PriceInEachProduct.setText(price);
        binding.ratingInEachProduct.setText(String.valueOf(rating));


        binding.EvaluationInEachProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 각 항목으로 이어져야함
            }
        });

        binding.BasicInformationInEachProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 기본정보로 들어가야함
            }
        });

        productType = 1;

        switch (productType) {
            case 1: // 핸드폰
                featureList.add("배터리");
                featureList.add("성능");
                featureList.add("카메라");
                featureList.add("무게");
                featureList.add("기타 특징");
                break;

            case 2: // 노트북
                featureList.add("화면");
                featureList.add("성능");
                featureList.add("무게");
                featureList.add("호환성");
                featureList.add("웹캠");
                featureList.add("배터리");
                featureList.add("기타 특징");
                break;

            case 3: // 태블릿
                featureList.add("화면");
                featureList.add("배터리");
                featureList.add("무게");
                featureList.add("펜슬");
                featureList.add("기타 특징");
                break;

            default: // 알 수 없는 productType
                featureList.add("등록되지 않은 상품 유형입니다.");
                break;
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new EachProductAdapter(featureList));

    }

    public class EachProductViewHolder extends RecyclerView.ViewHolder{

        EachProductPropertyBinding binding;


        public EachProductViewHolder(EachProductPropertyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public class EachProductAdapter extends RecyclerView.Adapter<EachProductViewHolder> {

        private List<String> featureList;

        public EachProductAdapter(List<String> featureList) {
            this.featureList = featureList;
        }

        @NonNull
        @Override
        public EachProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            EachProductPropertyBinding binding = EachProductPropertyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EachProductViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull EachProductViewHolder holder, int position) {
            String feature = featureList.get(position);
            holder.binding.property.setText(feature);
        }

        @Override
        public int getItemCount() {
            return featureList.size();
        }
    }


}