package com.example.easyspec.Profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.easyspec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends DialogFragment {
    private EditText editTextNewPassword; // 새 비밀번호 입력 필드
    private Button buttonUpdatePassword, buttonCancel; // 비밀번호 업데이트 및 취소 버튼

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false); // 레이아웃 설정

        // UI 요소 초기화
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        buttonUpdatePassword = view.findViewById(R.id.buttonUpdatePassword);
        buttonCancel = view.findViewById(R.id.buttonCancel);

        // 비밀번호 업데이트 버튼 클릭 시 updatePassword 메서드 호출
        buttonUpdatePassword.setOnClickListener(v -> updatePassword());
        // 취소 버튼 클릭 시 다이얼로그 종료
        buttonCancel.setOnClickListener(v -> {
            Log.d("EasySpec", "Password change dialog was canceled."); // 취소 로그
            dismiss();
        });

        return view;
    }

    private void updatePassword() {
        String newPassword = editTextNewPassword.getText().toString(); // 입력된 새 비밀번호 가져오기

        // 비밀번호 유효성 검사
        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
            Toast.makeText(getContext(), "at least 6", Toast.LENGTH_SHORT).show();
            Log.d("EasySpec", "Password validation failed: " + newPassword); // 유효성 검사 실패 로그
            return; // 비밀번호가 유효하지 않으면 메서드 종료
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 현재 사용자 가져오기
        if (user != null) {
            // 비밀번호 업데이트 요청
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                    dismiss(); // 다이얼로그 종료
                    Log.d("EasySpec", "Password change successful"); // 비밀번호 변경 성공 로그
                } else {
                    // 비밀번호 변경 실패 시 에러 메시지 출력
                    Toast.makeText(getContext(), "fail " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("EasySpec", "Password change failed: " + task.getException()); // 비밀번호 변경 실패 로그
                }
            });
        } else {
            Log.e("EasySpec", "No current user information available."); // 사용자 정보가 없을 경우 로그
        }
    }
}
