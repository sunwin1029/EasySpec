package com.example.easyspec;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.easyspec.databinding.ActivityUserProfileBinding;

public class UserProfile extends AppCompatActivity {

    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        // ViewBinding 초기화 및 레이아웃 설정
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Spinner 초기화
        Spinner universitySpinner = binding.universitySpinner;

        // strings.xml에서 대학 목록 배열을 가져옴
        String[] universities = getResources().getStringArray(R.array.university_list);

        // ArrayAdapter를 사용하여 Spinner에 항목 추가
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, universities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Spinner에 어댑터 설정
        universitySpinner.setAdapter(adapter);
    }
}