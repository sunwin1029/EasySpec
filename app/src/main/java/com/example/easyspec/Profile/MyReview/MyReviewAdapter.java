package com.example.easyspec.Profile.MyReview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyspec.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

// 리뷰 항목을 RecyclerView에 표시하는 어댑터 클래스
public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ViewHolder> {
    private List<ReviewItem> reviewItemList; // 리뷰 데이터를 담는 리스트
    private Context context; // 현재 액티비티 또는 프래그먼트의 컨텍스트

    // 생성자: 리뷰 데이터와 컨텍스트를 초기화
    public MyReviewAdapter(List<ReviewItem> reviewItemList, Context context) {
        this.reviewItemList = reviewItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // XML 레이아웃을 View 객체로 변환 (item_my_review 레이아웃 사용)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_review, parent, false);
        return new ViewHolder(view); // 뷰 홀더 생성
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 현재 위치의 리뷰 데이터 가져오기
        ReviewItem reviewItem = reviewItemList.get(position);

        // 제품 이미지 설정
        loadProductImage(reviewItem.getProductId(), holder.productImage);

        // 제품명 설정
        holder.productName.setText(reviewItem.getName());
        // 리뷰 내용 설정
        holder.reviewText.setText(reviewItem.getReviewText());
        // 좋아요 점수 설정
        holder.reviewScore.setText(String.valueOf(reviewItem.getlikes()));
        holder.reviewFeature.setText(reviewItem.getReviewFeature());

        // 수정 버튼 클릭 리스너 설정
        holder.btnEdit.setOnClickListener(v -> {
            // 수정 기능: ReviewFragment로 이동
            MyReviewFragment reviewFragment = MyReviewFragment.newInstance(
                    reviewItem.getProductId(),
                    reviewItem.getFeature(), // 리뷰의 기능 정보
                    reviewItem.getUserId() // 리뷰 작성자 정보
            );

            // 다이얼로그 프래그먼트 표시
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            reviewFragment.show(fragmentManager, "ReviewDialog");
        });

        // 삭제 버튼 클릭 리스너 설정
        holder.btnDelete.setOnClickListener(v -> {
            // Firebase에서 리뷰 데이터를 삭제
            String reviewKey = reviewItem.getKey(); // 리뷰의 고유 키 가져오기
            if (reviewKey != null && !reviewKey.isEmpty()) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                        .getReference("Reviews") // Firebase 경로: "Reviews"
                        .child(reviewKey); // 리뷰 고유 키를 경로로 설정

                // Firebase에서 리뷰 삭제
                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Firebase에서 삭제 성공 시
                        int position2 = holder.getAdapterPosition(); // 현재 아이템 위치
                        if (position2 != RecyclerView.NO_POSITION) {
                            reviewItemList.remove(position2); // 리스트에서 해당 리뷰 삭제
                            notifyItemRemoved(position2); // RecyclerView 갱신

                            // 삭제 완료 메시지 표시
                            Toast.makeText(context, "Review has been deleted.", Toast.LENGTH_SHORT).show();

                            // 리뷰 목록 새로고침 (MyReviewActivity가 컨텍스트일 경우)
                            if (context instanceof MyReviewActivity) {
                                ((MyReviewActivity) context).reloadReviews(); // 새로고침 메서드 호출
                            }
                        }
                    } else {
                        // Firebase 삭제 실패 시 에러 메시지 표시
                        Toast.makeText(context, "Failed to delete review: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // 리뷰 키가 유효하지 않은 경우
                Toast.makeText(context, "Invalid review key.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductImage(String productId, ImageView productImage) {
        String imageName = productId.toLowerCase(); // product1, product2 등
        int imageResId = productImage.getContext().getResources().getIdentifier(
                imageName, "drawable", productImage.getContext().getPackageName()
        );

        Log.d("EasySpec", "ProductId: " + productId + ", ImageName: " + imageName + ", ResId: " + imageResId); // Log added

        if (imageResId != 0) {
            productImage.setImageResource(imageResId); // 이미지 설정
        } else {
            productImage.setImageResource(R.drawable.iphone15_promax); // 기본 이미지 설정
        }
    }

    @Override
    public int getItemCount() {
        // 리뷰 리스트 크기 반환
        return reviewItemList.size();
    }

    // RecyclerView 아이템의 뷰를 관리하는 ViewHolder 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage; // 제품 이미지
        TextView productName, reviewText, reviewScore, reviewFeature; // 제품명, 리뷰 내용, 리뷰 점수
        ImageButton btnEdit, btnDelete; // 수정 버튼, 삭제 버튼

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // XML에서 뷰 초기화
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            reviewText = itemView.findViewById(R.id.review_text);
            reviewScore = itemView.findViewById(R.id.review_score);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            reviewFeature = itemView.findViewById(R.id.review_feature);
        }
    }
}
