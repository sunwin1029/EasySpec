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
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.databinding.EachProductLayoutBinding;
import com.example.easyspec.databinding.HeaderInEachProductBinding;
import com.example.easyspec.databinding.PropertyInEachProductBinding;

public class EachProduct extends AppCompatActivity {

    private final int TYPE_HEADER = 0;
    private final int TYPE_PROPERTY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EachProductLayoutBinding binding = EachProductLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private class PropertyViewHolderInEachProduct extends RecyclerView.ViewHolder {
        private PropertyInEachProductBinding binding;

        public PropertyViewHolderInEachProduct(PropertyInEachProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private class HeaderViewHolderInEachProduct extends RecyclerView.ViewHolder {
        private HeaderInEachProductBinding binding;
        public HeaderViewHolderInEachProduct(HeaderInEachProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private class AdapterInEachProduct extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == TYPE_HEADER) {
                HeaderInEachProductBinding binding = HeaderInEachProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new HeaderViewHolderInEachProduct(binding);
            }
            else {
                PropertyInEachProductBinding binding = PropertyInEachProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new PropertyViewHolderInEachProduct(binding);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof HeaderViewHolderInEachProduct) {

            }
            else {

            }
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0) return TYPE_HEADER;
            else return TYPE_PROPERTY;
        }
    }
}