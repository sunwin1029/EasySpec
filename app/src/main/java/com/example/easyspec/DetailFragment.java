package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그가 설명 닫기 버튼을 통해서만 닫히도록 설정
        setCancelable(false);
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

            // 설명창 닫혔음을 알려서 버튼 활성화
            WordExplainFragment parentFragment = (WordExplainFragment) getParentFragment();
            if (parentFragment != null) {
                parentFragment.hideDescription();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 다이얼로그의 스타일을 변경 (필요시 추가)
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Dialog);

        // 다이얼로그의 크기 조정 (가로폭을 80%로 설정)
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();

            // 화면의 80% 너비로 설정
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 세로는 wrap_content로 설정
            window.setAttributes(params);  // 적용
        }
    }
}
