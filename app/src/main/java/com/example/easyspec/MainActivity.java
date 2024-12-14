package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.LogIn.LoginActivity;
import com.example.easyspec.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.core.view.GravityCompat;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth; //FirebaseAuth 인스턴스 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화 및 레이아웃 설정
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 서치버튼 클릭 시 필터링 화면으로 이동
        binding.searchButton.setOnClickListener(v -> {
            // 메인 레이아웃 숨기기
            binding.mainLayout.setVisibility(View.GONE);

            // FragmentContainer에 FilteringFragment 추가
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FilteringFragment())
                    .addToBackStack(null) // 뒤로가기 지원
                    .commit();
        });

        // 뒤로가기 시 메인 레이아웃 복구
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // 메인 레이아웃 다시 표시
                binding.mainLayout.setVisibility(View.VISIBLE);
            }
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
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        binding.button3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,MyReviewActivity.class);
            startActivity(intent);
        });

        binding.button4.setOnClickListener(v -> {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END);
            }

            binding.mainLayout.setVisibility(View.GONE);

            // WordExplainFragment를 호출하여 프래그먼트로 이동
            WordExplainFragment fragment = new WordExplainFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment) // 프래그먼트를 추가할 컨테이너 ID
                    .addToBackStack(null) // 백스택에 추가
                    .commit();
        });

        binding.button5.setOnClickListener(v -> {
            // LogoutFragment 띄우기
            LogoutFragment logoutFragment = new LogoutFragment();
            logoutFragment.show(getSupportFragmentManager(), "logoutFragment");
        });

        // Firebase Authentication 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        // 로그인 상태 확인
        checkLoginStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 로그인 상태를 다시 확인할 수 있습니다.
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // 사용자가 로그인하지 않은 경우 로그인 화면으로 이동
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // MainActivity 종료
        }
    }

}
