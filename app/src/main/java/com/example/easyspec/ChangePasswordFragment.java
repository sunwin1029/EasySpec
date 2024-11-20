package com.example.easyspec;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends DialogFragment {
    private EditText editTextNewPassword;
    private Button buttonUpdatePassword, buttonCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        buttonUpdatePassword = view.findViewById(R.id.buttonUpdatePassword);
        buttonCancel = view.findViewById(R.id.buttonCancel);

        buttonUpdatePassword.setOnClickListener(v -> updatePassword());
        buttonCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    private void updatePassword() {
        String newPassword = editTextNewPassword.getText().toString();

        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
            Toast.makeText(getContext(), "비밀번호는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "비밀번호 변경 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
