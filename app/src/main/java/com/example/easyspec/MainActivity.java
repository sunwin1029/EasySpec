package com.example.easyspec;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import com.example.easyspec.databinding.ActivityMainBinding;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화 및 레이아웃 설정
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 서치버튼 클릭 시 필터링 화면으로 이동
        binding.searchButton.setOnClickListener(v -> {
            FilteringFragment filteringFragment = new FilteringFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, filteringFragment)
                    .addToBackStack(null)
                    .commit();
        });


        // 사이드바 열기 버튼 클릭 시 사이드바 열기
        binding.openSidebarButton.setOnClickListener(v -> {
            // 오른쪽 사이드바 열기
            binding.drawerLayout.openDrawer(GravityCompat.END);
        });

        // 각 버튼 클릭 시 이벤트 설정
        binding.button1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
        });

        binding.button2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,StarList.class);
            startActivity(intent);
        });

        binding.button3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,MyReviewActivity.class);
            startActivity(intent);
        });

        binding.button4.setOnClickListener(v -> {
            WordExplainFragment fragment = new WordExplainFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        binding.button5.setOnClickListener(v -> {
            // LogoutFragment 띄우기
            LogoutFragment logoutFragment = new LogoutFragment();
            logoutFragment.show(getSupportFragmentManager(), "logoutFragment");
        });

    }
}
