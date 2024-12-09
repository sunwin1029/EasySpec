package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.DialogFragment;
import com.example.easyspec.databinding.FragmentDetailBinding;

public class DetailFragment extends DialogFragment {

    private FragmentDetailBinding binding;

    public static DetailFragment newInstance(String description) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("description", description); // description 값을 전달
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ViewBinding 초기화
        binding = FragmentDetailBinding.inflate(inflater, container, false);


        // 전달받은 description 값을 텍스트뷰에 설정
        if (getArguments() != null) {
            String description = getArguments().getString("description");
            binding.descriptionTextView.setText(description); // descriptionText는 TextView의 ID
        }

        // 설명닫기 버튼 클릭 시 다이얼로그 닫기
        binding.closeButton.setOnClickListener(v -> {
            dismiss(); // DialogFragment의 dismiss() 메서드를 사용하여 프래그먼트 닫기
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
