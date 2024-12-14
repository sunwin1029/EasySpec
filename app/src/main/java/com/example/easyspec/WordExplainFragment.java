package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
<<<<<<< HEAD
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
=======
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.easyspec.databinding.FragmentWordExplainBinding;
>>>>>>> 622f22c46438c090fef4008a5ef14d81a18df945
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

<<<<<<< HEAD
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_explain, container, false);

        // RecyclerView 초기화
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
=======
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
>>>>>>> 622f22c46438c090fef4008a5ef14d81a18df945

        // 데이터 불러오기
        String[] titles = getResources().getStringArray(R.array.item_titles);
        String[] subtitles = getResources().getStringArray(R.array.item_subtitles);
        String[] descriptions = getResources().getStringArray(R.array.descriptions);

        // 아이템 리스트 생성
<<<<<<< HEAD
        int size = Math.min(titles.length, Math.min(subtitles.length, Math.min(descriptions.length, imageResIds.length)));
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
=======
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
>>>>>>> 622f22c46438c090fef4008a5ef14d81a18df945
            int imageResId = imageResIds[i];
            String description = descriptions[i];
            items.add(new Item(titles[i], subtitles[i], description, imageResId));
        }

<<<<<<< HEAD
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
=======
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
>>>>>>> 622f22c46438c090fef4008a5ef14d81a18df945
    }
}
