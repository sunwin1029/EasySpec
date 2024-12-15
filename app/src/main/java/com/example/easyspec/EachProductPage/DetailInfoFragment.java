package com.example.easyspec.EachProductPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easyspec.R;
import com.example.easyspec.RecyclerViewActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailInfoFragment extends Fragment {

    private String productId;

    public static DetailInfoFragment newInstance(String productId) {
        DetailInfoFragment fragment = new DetailInfoFragment();
        Bundle args = new Bundle();
        args.putString("productId", productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_info, container, false);

        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }

        // UI 요소 초기화
        ImageView productImage = view.findViewById(R.id.productImageInDetail);
        TextView detailText = view.findViewById(R.id.detailText);
        ImageView formerButton = view.findViewById(R.id.formerButton);
        Button easyViewButton = view.findViewById(R.id.easyView);

        // Firebase에서 세부 정보 가져오기
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("ProductItems").child(productId);
        productRef.child("details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String details = snapshot.getValue(String.class);
                detailText.setText(details != null ? details : "정보를 가져올 수 없습니다.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                detailText.setText("데이터 로드 실패: " + error.getMessage());
            }
        });

        // 이미지 매핑
        String imageName = productId.toLowerCase(); // 예: product1
        int imageResId = getResources().getIdentifier(imageName, "drawable", requireContext().getPackageName());
        if (imageResId != 0) {
            productImage.setImageResource(imageResId);
        } else {
            productImage.setImageResource(R.drawable.iphone15_promax); // 기본 이미지 설정
        }

        // 이전 버튼 클릭 시 프래그먼트 종료
        formerButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 쉽게 보기 버튼 클릭 시 RecyclerViewActivity 실행
        easyViewButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RecyclerViewActivity.class);
            intent.putExtra("productId", productId); // 필요하면 데이터 전달
            startActivity(intent);
        });

        return view;
    }
}
