package com.example.easyspec.Review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easyspec.R;

public class RatingReviewFragment extends Fragment {

    private RatingBar ratingBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment의 레이아웃 설정
        View view = inflater.inflate(R.layout.fragment_rating_review, container, false);

        // RatingBar 참조
        ratingBar = view.findViewById(R.id.ratingBar);

        // RatingBar 값 변경 시 동작 정의
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                Toast.makeText(getContext(), "평점: " + rating + "점을 선택했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
