package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    private ListView productListView;
    private ArrayList<String> productNames = new ArrayList<>();
    private ArrayList<ProductItem> productList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productListView = findViewById(R.id.product_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
        productListView.setAdapter(adapter);

        // Firebase Database 참조
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("ProductItems");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productNames.clear();
                productList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    ProductItem product = productSnapshot.getValue(ProductItem.class);
                    if (product != null) {
                        product.setKey(productSnapshot.getKey());
                        productNames.add(product.getName());
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductItem selectedProduct = productList.get(position);

                // 프래그먼트로 전달
                ProductDetailFragment fragment = ProductDetailFragment.newInstance(
                        selectedProduct.getKey(),
                        selectedProduct.getName(),
                        selectedProduct.getPrice()
                );
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
