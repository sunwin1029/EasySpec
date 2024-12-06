//package com.example.easyspec;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.easyspec.Data.ProductItem;
//import com.example.easyspec.Firebase.FirebaseHelper;
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.util.List;
//
//public class MyReviewEditFragment extends Fragment {
//
//    // Review 데이터를 업데이트할 때 필요한 key 값 (제품 ID)
//    private static final String ARG_REVIEW_KEY = "review_key";
//
//    private String reviewKey; // 제품의 고유 ID (productId)
//    private EditText batteryReviewEditText, performanceReviewEditText;
//    private Button saveButton;
//
//    // ReviewEditFragment 를 새로운 인스턴스로 생성하면서 reviewKey를 전달
//    public static MyReviewEditFragment newInstance(String reviewKey) {
//        MyReviewEditFragment fragment = new MyReviewEditFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_REVIEW_KEY, reviewKey); // 전달받은 reviewKey를 번들에 담음
//        fragment.setArguments(args); // 프래그먼트에 인자 전달
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            // 인자로 전달된 reviewKey 값을 가져옴
//            reviewKey = getArguments().getString(ARG_REVIEW_KEY);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        // 프래그먼트의 UI를 생성
//        View view = inflater.inflate(R.layout.fragment_review_edit, container, false);
//
//        // 레이아웃에서 EditText와 버튼을 찾음
//        batteryReviewEditText = view.findViewById(R.id.editBatteryReview);
//        performanceReviewEditText = view.findViewById(R.id.editPerformanceReview);
//        saveButton = view.findViewById(R.id.saveButton);
//
//        // 저장 버튼 클릭 리스너 설정
//        saveButton.setOnClickListener(v -> saveReviewChanges());
//
//        return view;
//    }
//
//    // 리뷰 수정 내용을 FirebaseHelper를 사용해 업데이트하는 메서드
////    private void saveReviewChanges() {
////        // EditText에서 수정된 리뷰 내용 가져오기
////        String updatedBatteryReview = batteryReviewEditText.getText().toString();
////        String updatedPerformanceReview = performanceReviewEditText.getText().toString();
////
////        // 현재 로그인된 사용자의 ID 가져오기
////        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
////
////        // FirebaseHelper 사용하여 리뷰 수정
//////        FirebaseHelper.getInstance().updateReview(
//////                reviewKey, // 제품 ID
//////                currentUserId, // 사용자 ID
//////                updatedBatteryReview, // 수정된 배터리 리뷰
//////                updatedPerformanceReview, // 수정된 성능 리뷰
//////                new FirebaseHelper.FirebaseCallback() {
//////                    @Override
//////                    public void onSuccess(List<ProductItem> productItems) {
//////                        // 성공 시 사용자에게 알림 및 프래그먼트 종료
//////                        Toast.makeText(getContext(), "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT).show();
//////                        requireActivity().getSupportFragmentManager().popBackStack();
//////                    }
//////
//////                    @Override
//////                    public void onFailure(Exception e) {
//////                        // 실패 시 사용자에게 알림
//////                        Toast.makeText(getContext(), "리뷰 수정 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//////                    }
//////                }
//////        );
////    }
//}
