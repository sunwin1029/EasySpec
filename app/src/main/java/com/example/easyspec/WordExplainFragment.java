package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WordExplainFragment extends Fragment {

    private final int[] imageResIds = {
            R.drawable.cpu,
            R.drawable.ssd,
            R.drawable.gpu,
            R.drawable.ram,
            R.drawable.port,
            R.drawable.display,
            R.drawable.battery,
            R.drawable.camera
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_explain, container, false);

        // RecyclerView 초기화
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 데이터 불러오기
        String[] titles = getResources().getStringArray(R.array.item_titles);
        String[] subtitles = getResources().getStringArray(R.array.item_subtitles);
        String[] descriptions = getResources().getStringArray(R.array.descriptions);

        // 아이템 리스트 생성
        int size = Math.min(titles.length, Math.min(subtitles.length, Math.min(descriptions.length, imageResIds.length)));
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int imageResId = imageResIds[i];
            String description = descriptions[i];
            items.add(new Item(titles[i], subtitles[i], description, imageResId));
        }

        // 어댑터 설정
        recyclerView.setAdapter(new ItemAdapter(items, item -> {
            // 클릭 이벤트 처리
            DetailFragment fragment = DetailFragment.newInstance(item);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }));

        return view;
    }
}
