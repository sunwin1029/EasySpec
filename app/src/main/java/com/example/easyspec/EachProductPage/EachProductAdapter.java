package com.example.easyspec.EachProductPage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.R;
import com.example.easyspec.Review.ReviewFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EachProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_COLLAPSED = 0;
    private static final int TYPE_EXPANDED = 1;

    private List<String> featureList;
    private List<Boolean> expandedStates;
    private String productId;
    private String userId;

    public EachProductAdapter(List<String> featureList, String productId, String userId) {
        this.featureList = featureList;
        this.productId = productId;
        this.userId = userId;

        // 초기 상태: 모두 축소
        this.expandedStates = new ArrayList<>();
        for (int i = 0; i < featureList.size(); i++) {
            expandedStates.add(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return expandedStates.get(position) ? TYPE_EXPANDED : TYPE_COLLAPSED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_EXPANDED) {
            View view = inflater.inflate(R.layout.each_product_property_expanded, parent, false);
            return new ExpandedViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.each_product_property, parent, false);
            return new CollapsedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String feature = featureList.get(position);

        if (holder instanceof ExpandedViewHolder) {
            ExpandedViewHolder expandedHolder = (ExpandedViewHolder) holder;
            expandedHolder.featureText.setText(feature);

            // Inner RecyclerView 초기화
            setupInnerRecyclerView(expandedHolder.innerRecyclerView, feature);

            // 리뷰 작성 버튼 클릭 리스너 설정
            expandedHolder.reviewButton.setOnClickListener(v -> {
                ReviewFragment fragment = ReviewFragment.newInstance(productId, feature, userId);
                ((AppCompatActivity) expandedHolder.itemView.getContext())
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            });

            // 축소 버튼 클릭 리스너
            expandedHolder.expandButton.setOnClickListener(v -> {
                expandedStates.set(position, false);
                notifyItemChanged(position);
            });

        } else if (holder instanceof CollapsedViewHolder) {
            CollapsedViewHolder collapsedHolder = (CollapsedViewHolder) holder;
            collapsedHolder.featureText.setText(feature);

            // 확장 버튼 클릭 리스너
            collapsedHolder.expandButton.setOnClickListener(v -> {
                expandedStates.set(position, true);
                notifyItemChanged(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    // ViewHolder for collapsed state
    static class CollapsedViewHolder extends RecyclerView.ViewHolder {
        TextView featureText;
        View expandButton;

        public CollapsedViewHolder(@NonNull View itemView) {
            super(itemView);
            featureText = itemView.findViewById(R.id.property);
            expandButton = itemView.findViewById(R.id.expandButton);
        }
    }

    // ViewHolder for expanded state
    static class ExpandedViewHolder extends RecyclerView.ViewHolder {
        TextView featureText;
        View expandButton;
        Button reviewButton;
        RecyclerView innerRecyclerView;

        public ExpandedViewHolder(@NonNull View itemView) {
            super(itemView);
            featureText = itemView.findViewById(R.id.property);
            expandButton = itemView.findViewById(R.id.expandButton);
            reviewButton = itemView.findViewById(R.id.reviewButton);
            innerRecyclerView = itemView.findViewById(R.id.expandedRecyclerView); // Inner RecyclerView
        }
    }

    private void setupInnerRecyclerView(RecyclerView recyclerView, String feature) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        reviewsRef.orderByChild("productId").equalTo(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<InnerReviewItem> reviewItems = new ArrayList<>();
                        AtomicInteger pendingTasks = new AtomicInteger((int) snapshot.getChildrenCount());

                        for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                            String reviewId = reviewSnapshot.getKey(); // 리뷰 ID 가져오기
                            String reviewFeature = reviewSnapshot.child("feature").getValue(String.class);

                            if (feature.equals(reviewFeature)) {
                                String reviewText = reviewSnapshot.child("reviewText").getValue(String.class);
                                int likes = reviewSnapshot.child("likes").getValue(Integer.class) != null
                                        ? reviewSnapshot.child("likes").getValue(Integer.class)
                                        : 0;
                                String userId = reviewSnapshot.child("userId").getValue(String.class);

                                // userId로부터 university 조회
                                usersRef.child(userId).child("university")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                                String department = userSnapshot.getValue(String.class);
                                                reviewItems.add(new InnerReviewItem(
                                                        department != null ? department : "Unknown",
                                                        reviewText,
                                                        likes,
                                                        reviewId,
                                                        feature,
                                                        productId// reviewId 추가
                                                ));

                                                if (pendingTasks.decrementAndGet() == 0) {
                                                    // **좋아요 순으로 정렬**
                                                    reviewItems.sort((item1, item2) -> Integer.compare(item2.getGoodCount(), item1.getGoodCount()));
                                                    setupRecyclerViewWithData(recyclerView, reviewItems);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("setupInnerRecyclerView", "Failed to fetch user data: " + error.getMessage());
                                                if (pendingTasks.decrementAndGet() == 0) {
                                                    // **좋아요 순으로 정렬**
                                                    reviewItems.sort((item1, item2) -> Integer.compare(item2.getGoodCount(), item1.getGoodCount()));
                                                    setupRecyclerViewWithData(recyclerView, reviewItems);
                                                }
                                            }
                                        });
                            } else {
                                if (pendingTasks.decrementAndGet() == 0) {
                                    // **좋아요 순으로 정렬**
                                    reviewItems.sort((item1, item2) -> Integer.compare(item2.getGoodCount(), item1.getGoodCount()));
                                    setupRecyclerViewWithData(recyclerView, reviewItems);
                                }
                            }
                        }

                        if (pendingTasks.get() == 0) {
                            // **좋아요 순으로 정렬**
                            reviewItems.sort((item1, item2) -> Integer.compare(item2.getGoodCount(), item1.getGoodCount()));
                            setupRecyclerViewWithData(recyclerView, reviewItems);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("setupInnerRecyclerView", "Failed to fetch reviews: " + error.getMessage());
                        setupRecyclerViewWithData(recyclerView, new ArrayList<>());
                    }
                });
    }



    private void setupRecyclerViewWithData(RecyclerView recyclerView, List<InnerReviewItem> reviewItems) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));

        // 16dp 간격 추가
        int spaceInPixels = dpToPx(16, recyclerView.getContext());
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spaceInPixels));
        }
        if (reviewItems.isEmpty()) {
            recyclerView.setAdapter(new EmptyReviewAdapter());
        } else {
            recyclerView.setAdapter(new InnerReviewAdapter(reviewItems, userId));
        }

        recyclerView.requestLayout();
    }






    private void fetchUserDepartment(String userId, DepartmentCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.child(userId).child("university")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String department = snapshot.getValue(String.class);
                        callback.onDepartmentFetched(department != null ? department : "Unknown");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("EachProductAdapter", "사용자 정보를 가져오는 중 오류 발생: " + error.getMessage());
                        callback.onDepartmentFetched("Unknown");
                    }
                });
    }
    interface DepartmentCallback {
        void onDepartmentFetched(String department);
    }


    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

}
