package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewEditFragment extends Fragment {

    // Review 데이터를 업데이트할 때 필요한 key 값 (제품 ID)
    private static final String ARG_REVIEW_KEY = "review_key";

    private String reviewKey; // 제품의 고유 ID (productId)
    private EditText batteryReviewEditText, performanceReviewEditText;
    private Button saveButton;

    private DatabaseReference reviewRef; //Firebase 에서 리뷰를 수정할 경로를 참조

    // ReviewEditFragment 를 새로운 인스턴스로 생성하면서 reviewKey를 전달
    public static ReviewEditFragment newInstance(String reviewKey) {
        ReviewEditFragment fragment = new ReviewEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REVIEW_KEY, reviewKey); //전달받은 reviewKey를 번들에 담음
        fragment.setArguments(args); //프래드먼트에 인자 전달
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // 인자로 전달된 reviewKey 값을 가져옴
            reviewKey = getArguments().getString(ARG_REVIEW_KEY);
        }

        // Firebase 경로 설정: reviews/{productId}/{userId}
        // 현재 로그인된 사용자의 ID를 가져와서 해당 경로를 설정
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reviewRef = FirebaseDatabase.getInstance()
                .getReference("reviews") // Firebase의 "reviews"경로
                .child(reviewKey) // 제품의 ID를 key로 설정(productId)
                .child(currentUserId); // 사용자의 ID를 key로 설정 (userId)
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 프래그먼트의 UI를 생성
        View view = inflater.inflate(R.layout.fragment_review_edit, container, false);

        // 레이아웃에서 EditText와 버튼을 찾음
        batteryReviewEditText = view.findViewById(R.id.editBatteryReview);
        performanceReviewEditText = view.findViewById(R.id.editPerformanceReview);
        saveButton = view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveReviewChanges());

        return view;
    }

    // 리뷰 수정 내용을 Firebase에 업데이트하는 메서드
    private void saveReviewChanges() {
        // EditText에서 수정된 리뷰 내용 가져오기
        String updatedBatteryReview = batteryReviewEditText.getText().toString();
        String updatedPerformanceReview = performanceReviewEditText.getText().toString();

        // Firebase 업데이트
        reviewRef.child("batteryReview").setValue(updatedBatteryReview)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewRef.child("performanceReview").setValue(updatedPerformanceReview)
                                .addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful()) {
                                        Toast.makeText(getContext(), "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                        requireActivity().getSupportFragmentManager().popBackStack(); // 프래그먼트 종료
                                    } else {
                                        Toast.makeText(getContext(), "성능 리뷰 업데이트 실패.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "배터리 리뷰 업데이트 실패.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
