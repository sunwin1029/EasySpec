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

        // SearchView 초기화 메서드 호출
        initSearchView();

        // 서치버튼 클릭 시 필터링 화면으로 이동
        binding.search.setOnClickListener(v -> {
            // 필터링 화면으로 이동
            Intent intent = new Intent(MainActivity.this, Filtering.class);
            startActivity(intent);
        });

        // 사이드바 열기 버튼 클릭 시 사이드바 열기
        binding.openSidebarButton.setOnClickListener(v -> {
            // 오른쪽 사이드바 열기
            binding.drawerLayout.openDrawer(GravityCompat.END);
        });

        // 각 버튼 클릭 시 이벤트 설정
        binding.button1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,UserProfile.class);
            startActivity(intent);
        });

        binding.button2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,StarList.class);
            startActivity(intent);
        });

        binding.button3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,MyReview.class);
            startActivity(intent);
        });

        // "리스트 보기" 버튼 클릭 시 RecyclerViewActivity로 이동
        binding.goToRecyclerviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
            startActivity(intent);
        });
    }

    private void initSearchView() {
        // SearchView 초기화
        binding.search.setSubmitButtonEnabled(true);
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // @TODO
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // @TODO
                return true;
            }
        });
        // 검색창 클릭 시 키보드 표시
        binding.search.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }
}
