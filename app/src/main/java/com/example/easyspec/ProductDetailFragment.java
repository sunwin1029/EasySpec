package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductDetailFragment extends Fragment {

    private static final String ARG_KEY = "key";
    private static final String ARG_NAME = "name";
    private static final String ARG_PRICE = "price";

    private String key;
    private String name;
    private int price;

    public static ProductDetailFragment newInstance(String key, String name, int price) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        args.putString(ARG_NAME, name);
        args.putInt(ARG_PRICE, price);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            key = getArguments().getString(ARG_KEY);
            name = getArguments().getString(ARG_NAME);
            price = getArguments().getInt(ARG_PRICE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        TextView nameTextView = view.findViewById(R.id.product_name);
        TextView priceTextView = view.findViewById(R.id.product_price);
        EditText batteryReviewEditText = view.findViewById(R.id.battery_review);
        EditText performanceReviewEditText = view.findViewById(R.id.performance_review);
        Button saveButton = view.findViewById(R.id.save_button);

        nameTextView.setText(name);
        priceTextView.setText(String.valueOf(price));

        saveButton.setOnClickListener(v -> {
            String batteryReview = batteryReviewEditText.getText().toString();
            String performanceReview = performanceReviewEditText.getText().toString();

            DatabaseReference productRef = FirebaseDatabase.getInstance()
                    .getReference("ProductItems")
                    .child(key);
            productRef.child("batteryReview").setValue(batteryReview);
            productRef.child("performanceReview").setValue(performanceReview);
        });

        return view;
    }
}
