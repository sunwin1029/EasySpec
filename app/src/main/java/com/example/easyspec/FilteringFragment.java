package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easyspec.Data.SearchData;
import com.google.firebase.auth.FirebaseAuth;

public class FilteringFragment extends Fragment {

    private Spinner deviceTypeSpinner;
    private EditText productNameEditText, minPriceEditText, maxPriceEditText;
    private GridLayout manufacturerGrid;
    private String selectedDeviceType = ""; // Spinner에서 선택된 기기 종류
    private int selectedManufacturerIndex = -1; // 제조사 선택 상태 (-1: 미선택)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_filtering.xml 레이아웃을 inflate하여 뷰 생성
        View view = inflater.inflate(R.layout.fragment_filtering, container, false);

        // UI 요소 초기화
        deviceTypeSpinner = view.findViewById(R.id.device_type_spinner);
        productNameEditText = view.findViewById(R.id.productNameEditText);
        minPriceEditText = view.findViewById(R.id.min_price);
        maxPriceEditText = view.findViewById(R.id.max_price);
        manufacturerGrid = view.findViewById(R.id.manufacturer_radio_group);

        // 검색 버튼 클릭 리스너 설정
        view.findViewById(R.id.search_button).setOnClickListener(v -> fetchFilteredProducts());

        // Spinner 설정
        setupDeviceTypeSpinner();

        // 제조사 Grid 설정
        setupManufacturerGrid();

        // 제품명 검색창 TextWatcher 추가
        productNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 가격 필터 리스너 설정
        setupFilterListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 뒤로가기 버튼 초기화 및 클릭 리스너 설정
        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // 프래그먼트를 백 스택에서 제거
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void setupDeviceTypeSpinner() {
        // Spinner 데이터 설정
        String[] deviceTypes = getResources().getStringArray(R.array.device_types);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                deviceTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceTypeSpinner.setAdapter(adapter);

        // Spinner 선택 리스너
        deviceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeviceType = position == 0 ? "" : parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupManufacturerGrid() {
        for (int i = 0; i < manufacturerGrid.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) manufacturerGrid.getChildAt(i);
            int index = i + 1;

            radioButton.setOnClickListener(v -> {
                if (selectedManufacturerIndex == index) {
                    radioButton.setChecked(false);
                    selectedManufacturerIndex = -1;
                } else {
                    selectedManufacturerIndex = index;
                    clearOtherRadioButtons(index);
                }
            });
        }
    }

    private void clearOtherRadioButtons(int exceptIndex) {
        for (int i = 0; i < manufacturerGrid.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) manufacturerGrid.getChildAt(i);
            int index = i + 1;
            if (index != exceptIndex) {
                radioButton.setChecked(false);
            }
        }
    }

    private void setupFilterListeners() {
        // 가격 필터 EditText 리스너 설정
        minPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        maxPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchFilteredProducts() {
        // Firebase Authentication을 통해 사용자 ID 가져오기
        String userId;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(getContext(), "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 입력 데이터 수집
        String productName = productNameEditText.getText().toString();
        int minPrice = minPriceEditText.getText().toString().isEmpty() ? -1 : Integer.parseInt(minPriceEditText.getText().toString());
        int maxPrice = maxPriceEditText.getText().toString().isEmpty() ? -1 : Integer.parseInt(maxPriceEditText.getText().toString());
        int deviceType = deviceTypeSpinner.getSelectedItemPosition(); // Spinner의 선택값

        // SearchData 객체 생성
        SearchData searchData = new SearchData(
                deviceType == 0 ? -1 : deviceType, // -1이면 선택 안 함
                productName.isEmpty() ? null : productName, // 제품명 없으면 null
                minPrice,
                maxPrice,
                selectedManufacturerIndex // 제조사 인덱스
        );

        // Intent로 데이터 전달
        Intent intent = new Intent(getActivity(), InventoryProductPage.class);
        intent.putExtra("searchData", searchData);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}
