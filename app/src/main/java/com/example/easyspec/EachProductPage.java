package com.example.easyspec;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


    public class EachProductAdapter extends RecyclerView.Adapter<EachProductAdapter.EachProductViewHolder> {

        private List<String> featureList;
        private List<Boolean> expandedStates; // 각 아이템의 확장 상태를 저장

        public EachProductAdapter(List<String> featureList) {
            this.featureList = featureList;
            this.expandedStates = new ArrayList<>();
            for (int i = 0; i < featureList.size(); i++) {
                expandedStates.add(false); // 초기에는 모두 확장되지 않은 상태
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

            // 기본 텍스트 설정
            holder.binding.property.setText(feature);

            // 확장 상태에 따라 추가 콘텐츠 표시/숨기기
            holder.binding.extraButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.binding.expandedRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            // 화살표 아이콘 변경
            holder.binding.expandButton.setImageResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);

            // 확장/축소 버튼 클릭 이벤트
            holder.binding.expandButton.setOnClickListener(v -> {
                boolean expand = !expandedStates.get(position); // 현재 상태 반전
                toggleExpandedState(position); // 상태 변경

                // 애니메이션 적용
                animateViewHeight(holder.binding.expandedRecyclerView, expand);

                notifyItemChanged(position); // 해당 아이템만 업데이트
            });

            // 확장된 RecyclerView에 데이터 설정 (예: 하위 항목들)
            if (isExpanded) {
                List<String> subItems = getSubItemsForFeature(feature); // 하위 항목 가져오기
                SubItemAdapter subItemAdapter = new SubItemAdapter(subItems);
                holder.binding.expandedRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                holder.binding.expandedRecyclerView.setAdapter(subItemAdapter);
            }
        }

        @Override
        public int getItemCount() {
            return featureList.size();
        }

        // 확장 상태를 토글
        private void toggleExpandedState(int position) {
            expandedStates.set(position, !expandedStates.get(position));
        }

        // 하위 항목 데이터를 가져오는 함수 (예제)
        private List<String> getSubItemsForFeature(String feature) {
            List<String> subItems = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                subItems.add(feature + " - SubItem " + i);
            }
            return subItems;
        }

        // ViewHolder 클래스
        public class EachProductViewHolder extends RecyclerView.ViewHolder {
            EachProductPropertyBinding binding;

            public EachProductViewHolder(EachProductPropertyBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

        // 하위 항목 RecyclerView Adapter
        public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {

            private List<String> subItems;

            public SubItemAdapter(List<String> subItems) {
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
                holder.textView.setText(subItems.get(position));
            }

            @Override
            public int getItemCount() {
                return subItems.size();
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

    // 애니메이션을 통해 RecyclerView의 높이를 변경
    private void animateViewHeight(View view, boolean expand) {
        int startHeight = expand ? 0 : view.getMeasuredHeight();
        int endHeight = expand ? 300 : 0;

        ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = value;
            view.setLayoutParams(params);
        });
        animator.setDuration(300); // 애니메이션 시간
        animator.start();
    }
}