package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Data.SearchData;
import com.example.easyspec.EachProductPage.EachProductPage;
import com.example.easyspec.databinding.ActivityInventoryProductPageBinding;
import com.example.easyspec.databinding.InventoryProductPageLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InventoryProductPage extends AppCompatActivity {

    private ActivityInventoryProductPageBinding binding;
    private List<ProductItem> productList = new ArrayList<>(); // 전체 데이터
    private List<ProductItem> filteredList = new ArrayList<>(); // 필터링된 데이터
    private InventoryAdapter adapter;

    private DatabaseReference databaseReference; // Firebase 참조
    private DatabaseReference userFavoritesRef; // Firebase 즐겨찾기 참조
    private ValueEventListener productListener; // 실시간 업데이트 리스너

    private String userId; // 사용자 ID
    private SearchData searchData; // 필터링 조건
    private String userUniversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryProductPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase 참조 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference("ProductItems");

        // Intent로 전달된 사용자 ID 및 SearchData 가져오기
        userId = getIntent().getStringExtra("userId");
        searchData = (SearchData) getIntent().getSerializableExtra("searchData");

        userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("favorites");

        // RecyclerView 초기화
        adapter = new InventoryAdapter(filteredList);
        binding.productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productRecyclerView.setAdapter(adapter);

        // 사용자 대학 정보 로드 후, 데이터 로드
        fetchUserUniversity(() -> {
            // 대학 정보 로드 완료 후, 제품 데이터를 가져옵니다.
            fetchProductData();
        });

        binding.check.setOnClickListener(view -> showSortDialog());
    }


    private void fetchProductData() {
        if (productListener != null) {
            databaseReference.removeEventListener(productListener);
        }

        productListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String productId = productSnapshot.getKey();
                    ProductItem productItem = createProductItemFromSnapshot(productSnapshot);
                    productItem.setId(productId);
                    productList.add(productItem);
                }

                // 필터링 적용
                applyFilters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("InventoryProductPage", "Failed to load data", error.toException());
            }
        };

        databaseReference.addValueEventListener(productListener);
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

        // UserNum 데이터 읽기
        int it = snapshot.child("UserNum/IT").getValue(Integer.class) != null ?
                snapshot.child("UserNum/IT").getValue(Integer.class) : 0;
        int english = snapshot.child("UserNum/English").getValue(Integer.class) != null ?
                snapshot.child("UserNum/English").getValue(Integer.class) : 0;
        int naturalScience = snapshot.child("UserNum/NaturalScience").getValue(Integer.class) != null ?
                snapshot.child("UserNum/NaturalScience").getValue(Integer.class) : 0;
        int economicsAndTrade = snapshot.child("UserNum/EconomicsAndTrade").getValue(Integer.class) != null ?
                snapshot.child("UserNum/EconomicsAndTrade").getValue(Integer.class) : 0;
        int law = snapshot.child("UserNum/Law").getValue(Integer.class) != null ?
                snapshot.child("UserNum/Law").getValue(Integer.class) : 0;
        int socialScience = snapshot.child("UserNum/SocialScience").getValue(Integer.class) != null ?
                snapshot.child("UserNum/SocialScience").getValue(Integer.class) : 0;

        return new ProductItem(
                name, price, null, productType, company, "Features",
                totalRating, ratingCount, false, null,
                it, english, naturalScience, economicsAndTrade, law, socialScience
        );
    }

    private void applyFilters() {
        if (searchData == null || (searchData.getName() == null
                && searchData.getMinimumPrice() == -1
                && searchData.getMaxPrice() == -1
                && searchData.getProductType() == -1
                && searchData.getCompany() == -1)) {
            filteredList.clear();
            filteredList.addAll(productList); // 모든 데이터를 표시
        } else {
            filteredList.clear();

            for (ProductItem product : productList) {
                boolean matches = true;

                if (searchData.getName() != null && !searchData.getName().isEmpty()) {
                    String productName = product.getName().toLowerCase();
                    String searchName = searchData.getName().toLowerCase();

                    matches &= productName.contains(searchName);
                }

                if (searchData.getMinimumPrice() != -1) {
                    matches &= product.getPrice() >= searchData.getMinimumPrice();
                }

                if (searchData.getMaxPrice() != -1) {
                    matches &= product.getPrice() <= searchData.getMaxPrice();
                }

                if (searchData.getProductType() != -1) {
                    matches &= product.getProductType() == searchData.getProductType();
                }

                if (searchData.getCompany() != -1) {
                    matches &= getCompanyCode(product.getCompany()) == searchData.getCompany();
                }

                if (matches) {
                    filteredList.add(product);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private int getCompanyCode(String company) {
        switch (company.toLowerCase()) {
            case "samsung":
                return 1;
            case "apple":
                return 2;
            case "lg":
                return 3;
            case "샤오미":
                return 4;
            case "레노버":
                return 5;
            case "asus":
                return 6;
            default:
                return -1;
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
        private final List<ProductItem> list;

        public InventoryAdapter(List<ProductItem> list) {
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
            String productId = productItem.getId();
            String productName = productItem.getName();

            holder.binding.productName.setText(productName);
            holder.binding.productPrice.setText(String.format("₩%,d", productItem.getPrice()));
            holder.binding.ratingText.setText(String.format("%.1f", productItem.getAverageRating()));

            if (userUniversity != null && !userUniversity.isEmpty()) {
                switch (userUniversity) {
                    case "IT":
                        holder.binding.reviewCount.setText(String.format("%d명", productItem.getIT()));
                        break;
                    case "English":
                        holder.binding.reviewCount.setText(String.format("%d명", productItem.getEnglish()));
                        break;
                    case "NaturalScience":
                        holder.binding.reviewCount.setText(String.format("%d명", productItem.getNaturalScience()));
                        break;
                    case "EconomicsAndTrade":
                        holder.binding.reviewCount.setText(String.format("%d명", productItem.getEconomicsAndTrade()));
                        break;
                    case "Law":
                        holder.binding.reviewCount.setText(String.format("%d명", productItem.getLaw()));
                        break;
                    case "SocialScience":
                        holder.binding.reviewCount.setText(String.format("%d명", productItem.getSocialScience()));
                        break;
                    default:
                        Toast.makeText(InventoryProductPage.this, "check1", Toast.LENGTH_SHORT).show();
                        holder.binding.reviewCount.setText("0명"); // 예외 처리
                }
            } else {
                Toast.makeText(InventoryProductPage.this, "check2", Toast.LENGTH_SHORT).show();
                holder.binding.reviewCount.setText("0명"); // 대학 정보가 없을 경우
            }
            DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("favorites");

            // Firebase에서 즐겨찾기 상태 확인
            userFavoritesRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean[] isFavorite = {snapshot.exists()}; // 즐겨찾기 상태 초기화
                    updateFavoriteIcon(holder, isFavorite[0]); // 아이콘 상태 설정

                    // 즐겨찾기 버튼 클릭 리스너
                    holder.binding.heartIcon.setOnClickListener(view -> {
                        if (isFavorite[0]) {
                            // 즐겨찾기 해제
                            userFavoritesRef.child(productId).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        isFavorite[0] = false; // 상태 업데이트
                                        updateFavoriteIcon(holder, false); // UI 업데이트
                                        Toast.makeText(holder.itemView.getContext(),
                                                productName + " removed from favorites.",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Log.e("Favorites", "Failed to remove favorite", e));
                        } else {
                            // 즐겨찾기 등록
                            userFavoritesRef.child(productId).setValue(productName)
                                    .addOnSuccessListener(aVoid -> {
                                        isFavorite[0] = true; // 상태 업데이트
                                        updateFavoriteIcon(holder, true); // UI 업데이트
                                        Toast.makeText(holder.itemView.getContext(),
                                                productName + " added to favorites.",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Log.e("Favorites", "Failed to add favorite", e));
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Favorites", "Failed to load favorite status", error.toException());
                }
            });

            // 상세 보기로 이동
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(holder.itemView.getContext(), EachProductPage.class);
                intent.putExtra("productId", productId);
                intent.putExtra("userId", userId);
                holder.itemView.getContext().startActivity(intent);
            });
        }

        private void updateFavoriteIcon(InventoryViewHolder holder, boolean isFavorite) {
            holder.binding.heartIcon.setImageResource(
                    isFavorite ? R.drawable.heart : R.drawable.heart_empty
            );
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    private void fetchUserUniversity(Runnable onComplete) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("university");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userUniversity = snapshot.getValue(String.class);
                if (userUniversity == null) {
                    userUniversity = ""; // 기본값 설정 (예외 처리)
                }
                // 콜백 호출
                onComplete.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("InventoryProductPage", "Failed to fetch user university", error.toException());
            }
        });
    }
    private void showSortDialog() {
        // 정렬 옵션 배열
        String[] sortOptions = {"가격순(가격높은순)", "가격순(가격낮은순)", "사용자순(소속 단과대)", "사용자순(전체 학생)", "별점순"};

        // 선택된 항목의 인덱스를 저장하는 변수
        final int[] selectedOption = {-1};

        // AlertDialog 생성
        new AlertDialog.Builder(this)
                .setTitle("정렬 기준 선택")
                .setSingleChoiceItems(sortOptions, -1, (dialog, which) -> {
                    // 선택된 항목의 인덱스 저장
                    selectedOption[0] = which;
                })
                .setPositiveButton("확인", (dialog, which) -> {
                    if (selectedOption[0] == -1) {
                        // 선택하지 않고 확인 버튼을 누른 경우
                        Toast.makeText(this, "정렬 기준을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 선택된 항목에 따라 정렬 수행
                        switch (selectedOption[0]) {
                            case 0: // 가격 높은순
                                filteredList.sort((a, b) -> Integer.compare(b.getPrice(), a.getPrice()));
                                break;
                            case 1: // 가격 낮은순
                                filteredList.sort((a, b) -> Integer.compare(a.getPrice(), b.getPrice()));
                                break;
                            case 2: // 사용자순(소속 단과대)
                                if (userUniversity != null && !userUniversity.isEmpty()) {
                                    filteredList.sort((a, b) -> Integer.compare(
                                            getUserCountByUniversity(b, userUniversity),
                                            getUserCountByUniversity(a, userUniversity)
                                    ));
                                } else {
                                    Toast.makeText(this, "사용자 소속 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                break;
                            case 3: // 사용자순(전체 학생)
                                filteredList.sort((a, b) -> Integer.compare(
                                        getTotalUserCount(b),
                                        getTotalUserCount(a)
                                ));
                                break;
                            case 4: // 별점순
                                filteredList.sort((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()));
                                break;
                        }

                        // RecyclerView 업데이트
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("취소", null) // 취소 버튼 추가
                .show();
    }

    private int getUserCountByUniversity(ProductItem item, String university) {
        switch (university) {
            case "IT":
                return item.getIT();
            case "English":
                return item.getEnglish();
            case "NaturalScience":
                return item.getNaturalScience();
            case "EconomicsAndTrade":
                return item.getEconomicsAndTrade();
            case "Law":
                return item.getLaw();
            case "SocialScience":
                return item.getSocialScience();
            default:
                return 0; // 예외 처리
        }
    }
    private int getTotalUserCount(ProductItem item) {
        return item.getIT() + item.getEnglish() + item.getNaturalScience() +
                item.getEconomicsAndTrade() + item.getLaw() + item.getSocialScience();
    }

}
