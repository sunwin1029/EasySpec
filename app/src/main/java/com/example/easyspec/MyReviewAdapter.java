package com.example.easyspec;

import android.content.Context;
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

import com.example.easyspec.Review.ReviewFragment;
import com.example.easyspec.ReviewItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ViewHolder> {
    private List<ReviewItem> reviewItemList;
    private Context context;

    public MyReviewAdapter(List<ReviewItem> reviewItemList, Context context) {
        this.reviewItemList = reviewItemList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewItem reviewItem = reviewItemList.get(position);

        // 제품 이미지 설정
        holder.productImage.setImageResource(reviewItem.getImageResource());
        holder.productName.setText(reviewItem.getProductId());
        holder.reviewText.setText(reviewItem.getReviewText()); // 리뷰 내용 설정
        holder.reviewScore.setText(String.valueOf(reviewItem.getlikes())); // 리뷰 점수 설정

        // 수정 버튼 클릭 리스너
        holder.btnEdit.setOnClickListener(v -> {
            // 수정하기 위한 로직 (ReviewFragment로 이동)
            ReviewFragment reviewFragment = ReviewFragment.newInstance(
                    reviewItem.getProductId(),
                    reviewItem.getFeature(), // Assuming ReviewItem has a getFeature method
                    reviewItem.getUserId() // Assuming ReviewItem has a getUserId method
            );

            // Navigate to the ReviewFragment
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, reviewFragment) // Ensure you have the correct container ID
                    .addToBackStack(null) // Allows users to navigate back
                    .commit();
        });

        // 삭제 버튼 클릭 리스너
        holder.btnDelete.setOnClickListener(v -> {
            // 삭제하기 위한 로직
            String reviewKey = reviewItem.getKey(); // 리뷰의 고유 키 가져오기
            if (reviewKey != null && !reviewKey.isEmpty()) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                        .getReference("Reviews") // 리뷰 데이터 경로
                        .child(reviewKey); // 리뷰의 고유 키

                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Firebase에서 성공적으로 삭제된 경우
                        reviewItemList.remove(holder.getAdapterPosition()); // 해당 아이템 리스트에서 삭제
                        notifyItemRemoved(holder.getAdapterPosition()); // RecyclerView 갱신
                        Toast.makeText(context, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 삭제 실패 시 처리
                        Toast.makeText(context, "리뷰 삭제에 실패했습니다: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "리뷰 키가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return reviewItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, reviewText, reviewScore;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image); // XML에서 이미지뷰 ID
            productName = itemView.findViewById(R.id.product_name); // 제품명 TextView
            reviewText = itemView.findViewById(R.id.review_text); // 리뷰 내용 TextView
            reviewScore = itemView.findViewById(R.id.review_score); // 리뷰 점수 TextView
            btnEdit = itemView.findViewById(R.id.btn_edit); // 수정 버튼 ID
            btnDelete = itemView.findViewById(R.id.btn_delete); // 삭제 버튼 ID
        }
    }
}
