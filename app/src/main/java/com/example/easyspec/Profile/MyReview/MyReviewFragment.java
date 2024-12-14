package com.example.easyspec.Profile.MyReview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.easyspec.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MyReviewFragment extends DialogFragment {

    private static final String ARG_PRODUCT_ID = "productId";
    private static final String ARG_FEATURE = "feature";
    private static final String ARG_USER_ID = "userId";

    private String productId;
    private String feature;
    private String userId;
    private String existingReviewId; // 기존 리뷰 ID
    private DatabaseReference reviewRef;

    public static MyReviewFragment newInstance(String productId, String feature, String userId) {
        MyReviewFragment fragment = new MyReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        args.putString(ARG_FEATURE, feature);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // 다이얼로그 크기 조정
        if (getDialog() != null && getDialog().getWindow() != null) {
            // 다이얼로그의 너비와 높이 설정 (비율 기반 또는 고정 크기)
            int width = (int) (400 * getResources().getDisplayMetrics().density);
            int height = (int) (700 * getResources().getDisplayMetrics().density);

            getDialog().getWindow().setLayout(width, height);
        }
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
        ImageView formerButton = view.findViewById(R.id.formerButton);

        reviewRef = FirebaseDatabase.getInstance().getReference("Reviews");

        setFeatureContent(feature, featureImage, textFeatureReview);

        // 기존 리뷰 확인 및 로드
        loadExistingReview(reviewText);

        // formerButton 클릭 시 다이얼로그 닫기
        formerButton.setOnClickListener(v -> dismiss());

        // nextButton 클릭 시 리뷰 저장 또는 업데이트
        nextButton.setOnClickListener(v -> {
            String reviewContent = reviewText.getText().toString().trim();
            if (!reviewContent.isEmpty()) {
                if (existingReviewId != null) {
                    updateReviewInDatabase(reviewContent);
                } else {
                    saveNewReviewToDatabase(reviewContent);
                }
                dismiss(); // 다이얼로그 닫기
            } else {
                Toast.makeText(requireContext(), "리뷰를 입력해주세요.", Toast.LENGTH_SHORT).show();
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
                imageRes = R.drawable.feature_camera;
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
                imageRes = R.drawable.feature_others;
                reviewQuestion = "이 특징은 어떤가요?";
                break;
        }

        featureImage.setImageResource(imageRes);
        textFeatureReview.setText(reviewQuestion);
    }

    private void loadExistingReview(TextView reviewText) {
        reviewRef.orderByChild("userId").equalTo(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String product = child.child("productId").getValue(String.class);
                        String featureName = child.child("feature").getValue(String.class);
                        if (productId.equals(product) && feature.equals(featureName)) {
                            existingReviewId = child.getKey();
                            String existingContent = child.child("reviewText").getValue(String.class);
                            reviewText.setText(existingContent);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ReviewDialogFragment", "Failed to load review", e));
    }

    private void saveNewReviewToDatabase(String reviewContent) {
        String reviewId = reviewRef.push().getKey(); // 새로운 리뷰 ID 생성

        if (reviewId != null) {
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("reviewText", reviewContent);
            reviewData.put("feature", feature);
            reviewData.put("productId", productId);
            reviewData.put("userId", userId);
            reviewData.put("likes", 0);
            reviewData.put("timestamp", System.currentTimeMillis());

            reviewRef.child(reviewId).setValue(reviewData)
                    .addOnSuccessListener(unused -> {
                        Log.d("ReviewDialogFragment", "리뷰 저장 성공!");
                        updateUserPoints(10);
                    })
                    .addOnFailureListener(e -> Log.e("ReviewDialogFragment", "리뷰 저장 실패", e));
        } else {
            Log.e("ReviewDialogFragment", "리뷰 ID 생성 실패");
        }
    }

    private void updateReviewInDatabase(String reviewContent) {
        if (existingReviewId != null) {
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("reviewText", reviewContent);
            updatedData.put("timestamp", System.currentTimeMillis()); // 업데이트 시간 추가

            reviewRef.child(existingReviewId).updateChildren(updatedData)
                    .addOnSuccessListener(unused -> Log.d("ReviewDialogFragment", "리뷰 업데이트 성공!"))
                    .addOnFailureListener(e -> Log.e("ReviewDialogFragment", "리뷰 업데이트 실패", e));
        } else {
            Log.e("ReviewDialogFragment", "기존 리뷰 ID를 찾을 수 없습니다.");
        }
    }

    private void updateUserPoints(int pointsToAdd) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("point");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long currentPoints = snapshot.getValue(Long.class); // 현재 포인트 값 가져오기
                if (currentPoints != null) {
                    userRef.setValue(currentPoints + pointsToAdd) // 포인트 추가
                            .addOnSuccessListener(unused -> Log.d("ReviewDialogFragment", "포인트 업데이트 성공!"))
                            .addOnFailureListener(e -> Log.e("ReviewDialogFragment", "포인트 업데이트 실패", e));
                } else {
                    userRef.setValue(pointsToAdd)
                            .addOnSuccessListener(unused -> Log.d("ReviewDialogFragment", "포인트 초기화 및 업데이트 성공!"))
                            .addOnFailureListener(e -> Log.e("ReviewDialogFragment", "포인트 초기화 실패", e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReviewDialogFragment", "포인트 정보 로드 실패", error.toException());
            }
        });
    }
}
