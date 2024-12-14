package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.easyspec.databinding.FragmentWordExplainBinding;
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

    private FragmentWordExplainBinding binding;
    private ItemAdapter itemAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // View Binding 초기화
        binding = FragmentWordExplainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 뒤로가기 버튼 클릭 이벤트 설정
        binding.backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 데이터 불러오기
        String[] titles = getResources().getStringArray(R.array.item_titles);
        String[] subtitles = getResources().getStringArray(R.array.item_subtitles);
        String[] descriptions = getResources().getStringArray(R.array.descriptions);

        // 아이템 리스트 생성
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            int imageResId = imageResIds[i];
            String description = descriptions[i];
            items.add(new Item(titles[i], subtitles[i], description, imageResId));
        }

        // ItemAdapter 초기화 및 리사이클러뷰 설정
        itemAdapter = new ItemAdapter(items, description -> {
            // 설명 보기 버튼 클릭 시 처리
            DetailFragment fragment = DetailFragment.newInstance(description);
            fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Dialog);
            fragment.show(getChildFragmentManager(), "DetailFragment");

            // 설명창 열렸으므로 버튼 비활성화
            itemAdapter.setDescriptionActive(true);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수 방지
    }

    // 설명창을 닫을 때 버튼 활성화
    public void hideDescription() {
        itemAdapter.setDescriptionActive(false);  // 설명창 닫을 때 버튼 다시 활성화
    }
}
