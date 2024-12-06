package com.example.easyspec;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.easyspec.databinding.ActivityRecyclerviewBinding;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private int[] imageResIds = {
            R.drawable.cpu,
            R.drawable.ssd, // no error
            R.drawable.gpu,
            R.drawable.ram,
            R.drawable.port,
            R.drawable.display,
            R.drawable.battery,
            R.drawable.camera
    };

    private ActivityRecyclerviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityRecyclerviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 데이터 불러오기
        String[] titles = getResources().getStringArray(R.array.item_titles);
        String[] subtitles = getResources().getStringArray(R.array.item_subtitles);
        String[] descriptions = getResources().getStringArray(R.array.descriptions);


        // 아이템 리스트 생성
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            // 배열 범위 체크 없이 직접 매칭
            int imageResId = imageResIds[i];
            String description = descriptions[i];

            items.add(new Item(titles[i], subtitles[i], description, imageResId));
        }

        // 리사이클러 뷰 설정
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new ItemAdapter(items, item -> {
            // 클릭 이벤트 처리
            DetailFragment fragment = DetailFragment.newInstance(item);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        }));
    }
}
