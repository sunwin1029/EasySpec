package com.example.easyspec.EachProductPage;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int space;

    public HorizontalSpaceItemDecoration(int space) {
        this.space = space; // 간격을 픽셀 단위로 설정
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) return;

        // 첫 번째 항목에는 간격을 추가하지 않음
        if (position == 0) {
            outRect.left = 0;
        } else {
            outRect.left = space; // 간격 설정
        }

        outRect.right = 0;
    }
}
