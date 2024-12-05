package com.example.easyspec.Review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easyspec.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ReviewFragment extends Fragment {

    private static final String ARG_PRODUCT_ID = "productId";
    private static final String ARG_FEATURE = "feature";
    private static final String ARG_USER_ID = "userId";

    private String productId;
    private String feature;
    private String userId;

    public static ReviewFragment newInstance(String productId, String feature, String userId) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        args.putString(ARG_FEATURE, feature);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
            feature = getArguments().getString(ARG_FEATURE);
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        ImageView featureImage = view.findViewById(R.id.featureImage);
        TextView textFeatureReview = view.findViewById(R.id.textFeatureReview);
        TextView reviewText = view.findViewById(R.id.reviewText);
        ImageView nextButton = view.findViewById(R.id.nextButton);
        ImageView formerButton = view.findViewById(R.id.formerButton); // formerButton 추가

        setFeatureContent(feature, featureImage, textFeatureReview);

        // formerButton 클릭 시 복귀
        formerButton.setOnClickListener(v -> {
            requireActivity().onBackPressed(); // 이전 화면(EachProductPage)으로 돌아가기
        });

        // nextButton 클릭 시 리뷰 저장 후 복귀
        nextButton.setOnClickListener(v -> {
            String reviewContent = reviewText.getText().toString().trim();
            if (!reviewContent.isEmpty()) {
                saveReviewToDatabase(reviewContent);
                Toast.makeText(getContext(), "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed(); // 이전 화면(EachProductPage)으로 복귀
            } else {
                Toast.makeText(getContext(), "리뷰를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setFeatureContent(String feature, ImageView featureImage, TextView textFeatureReview) {
        int imageRes;
        String reviewQuestion;

        switch (feature) {
            case "배터리":
                imageRes = R.drawable.feature_battery;
                reviewQuestion = "배터리는 얼마나 오래가나요?";
                break;
            case "카메라":
                imageRes = R.drawable.feature_camera;
                reviewQuestion = "카메라는 어느 정도로 잘 찍히나요?";
                break;
            case "호환성":
                imageRes = R.drawable.feature_compat;
                reviewQuestion = "어떤 기기와 연결이 용이한가요?";
                break;
            case "무게":
                imageRes = R.drawable.feature_weight;
                reviewQuestion = "무게 체감은 어떤가요?";
                break;
            case "펜슬":
                imageRes = R.drawable.feature_pencil;
                reviewQuestion = "태블릿의 펜은 어떤가요?";
                break;
            case "성능":
                imageRes = R.drawable.feature_performance;
                reviewQuestion = "성능은 어떤가요?";
                break;
            case "웹캠":
                imageRes = R.drawable.feature_camera; // 웹캠과 카메라가 동일 이미지라 가정
                reviewQuestion = "웹캠의 성능은 어떤가요?";
                break;
            case "화면":
                imageRes = R.drawable.feature_screen;
                reviewQuestion = "화면의 크기 및 품질은 어떤가요?";
                break;
            case "기타 특징":
                imageRes = R.drawable.feature_others;
                reviewQuestion = "기타 특징들은 어떤가요?";
                break;
            default:
                imageRes = R.drawable.feature_others; // 기본 이미지
                reviewQuestion = "이 특징은 어떤가요?";
                break;
        }

        featureImage.setImageResource(imageRes);
        textFeatureReview.setText(reviewQuestion);
    }

    private void saveReviewToDatabase(String reviewContent) {
        DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                .getReference("Reviews");

        String reviewId = reviewRef.push().getKey();

        if (reviewId != null) {
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("reviewText", reviewContent);
            reviewData.put("feature", feature);
            reviewData.put("productId", productId);
            reviewData.put("userId", userId);
            reviewData.put("likes", 0); // 초기 좋아요 수
            reviewData.put("timestamp", System.currentTimeMillis());

            reviewRef.child(reviewId).setValue(reviewData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "리뷰 저장 성공!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "리뷰 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "리뷰 ID 생성 실패!", Toast.LENGTH_SHORT).show();
        }
    }
}
