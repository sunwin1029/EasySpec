package com.example.easyspec.LogIn;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.easyspec.R;
import android.util.Log; // Log 클래스를 사용하기 위해 추가

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogoutFragment extends DialogFragment {

    public LogoutFragment() {
        // Required empty public constructor
    }

    public static LogoutFragment newInstance() {
        return new LogoutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 네 버튼 클릭 시 로그인 화면으로 이동
        view.findViewById(R.id.button_yes).setOnClickListener(v -> {
            Log.d("EasySpec", "Logout confirmed: Navigating to login screen"); // 로그 추가
            // 로그인 화면으로 이동
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish(); // 현재 Activity 종료
        });

        // 아니요 버튼 클릭 시 프래그먼트 닫기
        view.findViewById(R.id.button_no).setOnClickListener(v -> {
            Log.d("EasySpec", "Logout canceled: Closing fragment"); // 로그 추가
            dismiss(); // 프래그먼트 닫기
        });
    }
}
