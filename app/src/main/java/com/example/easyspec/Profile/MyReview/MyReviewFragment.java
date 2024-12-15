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

// 리뷰를 작성하거나 수정하는 다이얼로그 프래그먼트
public class MyReviewFragment extends DialogFragment {

    private static final String ARG_PRODUCT_ID = "productId"; // 제품 ID
    private static final String ARG_FEATURE = "feature"; // 리뷰 기능
    private static final String ARG_USER_ID = "userId"; // 사용자 ID

    private String productId; // 제품 ID
    private String feature; // 리뷰 기능
    private String userId; // 사용자 ID
    private String existingReviewId; // 기존 리뷰 ID
    private DatabaseReference reviewRef; // 리뷰 데이터베이스 참조

    // 새로운 인스턴스 생성
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
            int width = (int) (400 * getResources().getDisplayMetrics().density);
            int height = (int) (700 * getResources().getDisplayMetrics().density);
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 전달받은 인자 초기화
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

        // UI 요소 초기화
        ImageView featureImage = view.findViewById(R.id.featureImage);
        TextView textFeatureReview = view.findViewById(R.id.textFeatureReview);
        TextView reviewText = view.findViewById(R.id.reviewText);
        ImageView nextButton = view.findViewById(R.id.nextButton);
        ImageView formerButton = view.findViewById(R.id.formerButton);

        reviewRef = FirebaseDatabase.getInstance().getReference("Reviews"); // 리뷰 데이터베이스 참조

        // 기능에 맞는 내용 설정
        setFeatureContent(feature, featureImage, textFeatureReview);

        // 기존 리뷰 로드
        loadExistingReview(reviewText);

        // 이전 버튼 클릭 시 다이얼로그 닫기
        formerButton.setOnClickListener(v -> {
            dismiss();
        });

        // 다음 버튼 클릭 시 리뷰 저장 또는 업데이트
        nextButton.setOnClickListener(v -> {
            String reviewContent = reviewText.getText().toString().trim();
            if (!reviewContent.isEmpty()) {
                if (existingReviewId != null) {
                    updateReviewInDatabase(reviewContent); // 기존 리뷰 업데이트
                } else {
                    saveNewReviewToDatabase(reviewContent); // 새로운 리뷰 저장
                }
                dismiss(); // 다이얼로그 닫기
            } else {
                Toast.makeText(requireContext(), "리뷰를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // 기능에 따른 이미지 및 질문 텍스트 설정
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

    // 기존 리뷰 로드
    private void loadExistingReview(TextView reviewText) {
        reviewRef.orderByChild("userId").equalTo(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String product = child.child("productId").getValue(String.class);
                        String featureName = child.child("feature").getValue(String.class);
                        if (productId.equals(product) && feature.equals(featureName)) {
                            existingReviewId = child.getKey(); // 기존 리뷰 ID 저장
                            String existingContent = child.child("reviewText").getValue(String.class);
                            reviewText.setText(existingContent); // 리뷰 내용 설정
                            Log.d("EasySpec", "Successfully loaded existing review: " + existingContent);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("EasySpec", "Failed to load review", e));
    }

    // 새로운 리뷰를 데이터베이스에 저장
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
                        Log.d("EasySpec", "Review saved successfully!");
                        updateUserPoints(10); // 포인트 업데이트
                    })
                    .addOnFailureListener(e -> Log.e("EasySpec", "Failed to save review", e));
        } else {
            Log.e("EasySpec", "Failed to generate review ID");
        }
    }

    // 기존 리뷰를 업데이트
    private void updateReviewInDatabase(String reviewContent) {
        if (existingReviewId != null) {
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("reviewText", reviewContent);
            updatedData.put("timestamp", System.currentTimeMillis()); // 업데이트 시간 추가

            reviewRef.child(existingReviewId).updateChildren(updatedData)
                    .addOnSuccessListener(unused -> Log.d("EasySpec", "Review updated successfully!"))
                    .addOnFailureListener(e -> Log.e("EasySpec", "Failed to update review", e));
        } else {
            Log.e("EasySpec", "Could not find existing review ID.");
        }
    }

    // 사용자 포인트 업데이트
    private void updateUserPoints(int pointsToAdd) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("point");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long currentPoints = snapshot.getValue(Long.class); // 현재 포인트 값 가져오기
                if (currentPoints != null) {
                    userRef.setValue(currentPoints + pointsToAdd) // 포인트 추가
                            .addOnSuccessListener(unused -> Log.d("EasySpec", "Points updated successfully!"))
                            .addOnFailureListener(e -> Log.e("EasySpec", "Failed to update points", e));
                } else {
                    userRef.setValue(pointsToAdd)
                            .addOnSuccessListener(unused -> Log.d("EasySpec", "Points initialized and updated successfully!"))
                            .addOnFailureListener(e -> Log.e("EasySpec", "Failed to initialize points", e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EasySpec", "Failed to load points information", error.toException());
            }
        });
    }
}
