package com.example.easyspec.EachProductPage;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Data.Review;
import com.example.easyspec.R;
import com.example.easyspec.Review.RatingReviewFragment;
import com.example.easyspec.databinding.ActivityEachProductPageBinding;
import com.example.easyspec.databinding.EachProductPropertyBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EachProductPage extends AppCompatActivity {

    private static final String TAG = "EachProductPage";

    private DatabaseReference databaseReference; // Firebase 참조
    private ProductItem productItem; // 로드된 ProductItem 객체
    private List<String> featureList = new ArrayList<>(); // 리뷰 카테고리 리스트
    private ActivityEachProductPageBinding binding;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEachProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String productId = getIntent().getStringExtra("productId");
        userId = getIntent().getStringExtra("userId"); // 로컬 변수가 아닌 멤버 변수에 저장

        if (productId == null || userId == null) {
            Toast.makeText(this, "Missing essential data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchProductItemFromFirebase(productId);
    }


    private void setupButtonActions(String productId) {
        binding.EvaluationInEachProduct.setOnClickListener(v -> {
            RatingReviewFragment fragment = new RatingReviewFragment();
            Bundle args = new Bundle();
            args.putString("userId", userId); // 사용자 ID 전달
            args.putString("productId", productId); // 고유 ID 전달
            fragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }


    private void fetchProductItemFromFirebase(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance()
                .getReference("ProductItems")
                .child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(EachProductPage.this, "제품 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                productItem = createProductItemFromSnapshot(snapshot);
                updateUIWithProductItem(productItem);

                // 평가 버튼 설정 호출
                setupButtonActions(productId); // <- 여기에 호출 추가
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EachProductPage.this, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    private ProductItem createProductItemFromSnapshot(DataSnapshot snapshot) {
        String name = snapshot.child("name").getValue(String.class);
        int price = snapshot.child("price").getValue(Integer.class);
        String company = snapshot.child("Company").getValue(String.class);
        int productType = snapshot.child("productType").getValue(Integer.class);
        double totalRating = snapshot.child("rating/totalRating").getValue(Double.class) != null ?
                snapshot.child("rating/totalRating").getValue(Double.class) : 0.0;
        int ratingCount = snapshot.child("rating/ratingCount").getValue(Integer.class) != null ?
                snapshot.child("rating/ratingCount").getValue(Integer.class) : 0;

        ProductItem productItem = new ProductItem(
                name, price, null, productType, company, "Features",
                totalRating, ratingCount, false, null, 0, 0, 0, 0, 0, 0
        );

        // productId 설정
        productItem.setId(snapshot.getKey());
        return productItem;
    }


    private void updateUIWithProductItem(ProductItem item) {
        if (item == null) return;

        binding.NameInEachProduct.setText(item.getName());
        binding.PriceInEachProduct.setText(String.format("₩%,d", item.getPrice()));
        binding.ratingInEachProduct.setText(String.format("%.1f", item.getAverageRating()));
        binding.ImageInEachProduct.setImageResource(item.getImageResource() != null ?
                item.getImageResource() : R.drawable.iphone15_promax);
    }

    private void setupRecyclerView() {
        if (featureList.isEmpty()) {
            Log.e(TAG, "Feature list is empty. Cannot set up RecyclerView.");
            return;
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new EachProductAdapter(featureList));
    }

    public class EachProductAdapter extends RecyclerView.Adapter<EachProductAdapter.EachProductViewHolder> {

        private List<String> featureList;
        private List<Boolean> expandedStates;

        public EachProductAdapter(List<String> featureList) {
            this.featureList = featureList;
            this.expandedStates = new ArrayList<>();
            for (int i = 0; i < featureList.size(); i++) {
                expandedStates.add(false);
            }
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
            boolean isExpanded = expandedStates.get(position);

            holder.binding.property.setText(feature);

            holder.binding.extraButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.binding.expandedRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.binding.expandButton.setImageResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);

            holder.binding.expandButton.setOnClickListener(v -> {
                boolean expand = !expandedStates.get(position);
                expandedStates.set(position, expand);
                notifyItemChanged(position);
            });

            // 하위 리뷰 데이터 설정
            List<Review> subItems = productItem.getReviews().get(feature);
            if (subItems.isEmpty()) {
                holder.binding.extraButton.setVisibility(View.GONE);
            }

            if (isExpanded) {
                SubItemAdapter subItemAdapter = new SubItemAdapter(subItems);
                holder.binding.expandedRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                holder.binding.expandedRecyclerView.setAdapter(subItemAdapter);
            }
        }

        @Override
        public int getItemCount() {
            return featureList.size();
        }

        public class EachProductViewHolder extends RecyclerView.ViewHolder {
            EachProductPropertyBinding binding;

            public EachProductViewHolder(EachProductPropertyBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {

            private List<Review> subItems;

            public SubItemAdapter(List<Review> subItems) {
                this.subItems = subItems;
            }

            @NonNull
            @Override
            public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new SubItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull SubItemViewHolder holder, int position) {
                if (subItems.isEmpty()) {
                    holder.textView.setText("리뷰가 없습니다.");
                } else {
                    holder.textView.setText(subItems.get(position).getReviewText());
                }
            }

            @Override
            public int getItemCount() {
                return subItems.isEmpty() ? 1 : subItems.size();
            }

            public class SubItemViewHolder extends RecyclerView.ViewHolder {
                TextView textView;

                public SubItemViewHolder(@NonNull View itemView) {
                    super(itemView);
                    textView = itemView.findViewById(android.R.id.text1);
                }
            }
        }
    }
}
