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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Data.Review;
import com.example.easyspec.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEachProductPageBinding binding = ActivityEachProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Intent로 전달된 ProductItem 가져오기
        ProductItem productItem = (ProductItem) getIntent().getSerializableExtra("selectedProduct");

        if (productItem == null) {
            Toast.makeText(this, "Product data not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // UI 업데이트
        binding.NameInEachProduct.setText(productItem.getName());
        binding.PriceInEachProduct.setText(String.format("₩%,d", productItem.getPrice()));
        binding.ratingInEachProduct.setText(String.format("%.1f", productItem.getAverageRating()));

        // **이미지가 없는 경우 기본 이미지를 표시**
        binding.ImageInEachProduct.setImageResource(
                productItem.getImageResource() != null ? productItem.getImageResource() : R.drawable.ic_launcher_foreground);
    }


    private void fetchProductItemFromFirebase(String productName) {
        if (productName == null || productName.isEmpty()) {
            Log.e(TAG, "Product name is null or empty");
            return;
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean productFound = false;

                for (DataSnapshot category : snapshot.getChildren()) {
                    for (DataSnapshot child : category.getChildren()) {
                        String name = child.child("name").getValue(String.class);
                        if (name != null && name.equals(productName)) {
                            productItem = createProductItemFromSnapshot(child);
                            updateUIWithProductItem(productItem);
                            setupRecyclerView();
                            productFound = true;
                            break;
                        }
                    }
                    if (productFound) break;
                }

                if (!productFound) {
                    Log.e(TAG, "Product not found in Firebase");
                    Toast.makeText(EachProductPage.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load product data", error.toException());
            }
        });
    }

    private ProductItem createProductItemFromSnapshot(DataSnapshot snapshot) {
        // 기본 정보
        String name = snapshot.child("name").getValue(String.class);
        int price = snapshot.child("price").getValue(Integer.class);
        int productType = snapshot.child("productType").getValue(Integer.class);
        double totalRating = snapshot.child("rating/totalRating").getValue(Double.class) != null ?
                snapshot.child("rating/totalRating").getValue(Double.class) : 0.0;
        int ratingCount = snapshot.child("rating/ratingCount").getValue(Integer.class) != null ?
                snapshot.child("rating/ratingCount").getValue(Integer.class) : 0;
        boolean heart = snapshot.child("heart").getValue(Boolean.class) != null &&
                snapshot.child("heart").getValue(Boolean.class);

        // 리뷰 데이터
        Map<String, List<Review>> reviews = new HashMap<>();
        DataSnapshot reviewsSnapshot = snapshot.child("reviews");

        // 기본 속성 목록 정의
        String[] defaultFeatures = {"화면", "배터리", "무게", "성능", "호환성", "웹캠", "기타 특징"};

        // 데이터베이스에서 가져온 속성 추가
        for (DataSnapshot featureSnapshot : reviewsSnapshot.getChildren()) {
            String featureName = featureSnapshot.getKey();
            List<Review> featureReviews = new ArrayList<>();
            for (DataSnapshot reviewSnapshot : featureSnapshot.getChildren()) {
                String reviewText = reviewSnapshot.child("reviewText").getValue(String.class);
                String userId = reviewSnapshot.child("userId").getValue(String.class);
                int likes = reviewSnapshot.child("likes").getValue(Integer.class) != null ?
                        reviewSnapshot.child("likes").getValue(Integer.class) : 0;
                featureReviews.add(new Review(reviewText, userId, likes));
            }
            reviews.put(featureName, featureReviews);
            featureList.add(featureName);
        }

        // 기본 속성 추가 (리뷰 데이터가 없는 속성)
        for (String feature : defaultFeatures) {
            if (!reviews.containsKey(feature)) {
                reviews.put(feature, new ArrayList<>()); // 빈 리뷰 리스트 추가
                featureList.add(feature); // RecyclerView에 표시하기 위해 추가
            }
        }

        return new ProductItem(
                name, price, null, productType, "Company", "Features",
                totalRating, ratingCount, heart, reviews, 0, 0, 0, 0, 0, 0
        );
    }


    private void updateUIWithProductItem(ProductItem item) {
        if (item == null) return;

        binding.NameInEachProduct.setText(item.getName());
        binding.PriceInEachProduct.setText(String.valueOf(item.getPrice()));
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

    private void setupButtonActions() {
        binding.EvaluationInEachProduct.setOnClickListener(v -> {
            // 평가 버튼 클릭 시 동작 정의
        });

        binding.BasicInformationInEachProduct.setOnClickListener(v -> {
            // 기본정보 버튼 클릭 시 동작 정의
        });
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
