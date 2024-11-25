package com.example.easyspec;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.easyspec.Data.ProductItem;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<ProductItem> {

    private Context context;
    private List<ProductItem> productItems;

    public ProductAdapter(Context context, List<ProductItem> productItems) {
        super(context, R.layout.product_item, productItems);
        this.context = context;
        this.productItems = productItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // View 재사용을 위해 convertView 사용
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        }

        // 제품 데이터 가져오기
        ProductItem product = productItems.get(position);

        // 데이터 바인딩
        TextView nameTextView = convertView.findViewById(R.id.productName);
        TextView priceTextView = convertView.findViewById(R.id.productPrice);

        nameTextView.setText(product.getName());
        priceTextView.setText(String.format("%,d 원", product.getPrice()));

        // 클릭 시 이벤트 처리
        convertView.setOnClickListener(v -> {
            // 제품 클릭 시 처리 (예: ReviewWriteActivity로 이동)
            Intent intent = new Intent(context, ReviewWriteActivity.class);
            intent.putExtra("productName", product.getName());
            context.startActivity(intent);
        });

        return convertView;
    }
}
