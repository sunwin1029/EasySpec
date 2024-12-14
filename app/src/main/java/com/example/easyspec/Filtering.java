package com.example.easyspec;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyspec.Data.SearchData;
<<<<<<< HEAD
=======
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
>>>>>>> 622f22c46438c090fef4008a5ef14d81a18df945

public class Filtering extends AppCompatActivity {

    private static final String TAG = "Search_Filter";

    private Spinner deviceTypeSpinner;
    private EditText productNameEditText, minPriceEditText, maxPriceEditText;
    private GridLayout manufacturerGrid;
    private String selectedDeviceType = ""; // Spinner에서 선택된 기기 종류
    private boolean isFilterConditionActive = false; // 필터 조건 활성화 여부
    private int selectedManufacturerIndex = -1; // 제조사 선택 상태 (-1: 미선택)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_filtering);

        Button button=findViewById(R.id.back_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Filtering.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // UI 요소 초기화
        deviceTypeSpinner = findViewById(R.id.device_type_spinner);
        productNameEditText = findViewById(R.id.productNameEditText);
        minPriceEditText = findViewById(R.id.min_price);
        maxPriceEditText = findViewById(R.id.max_price);
        manufacturerGrid = findViewById(R.id.manufacturer_radio_group);

        findViewById(R.id.search_button).setOnClickListener(v -> fetchFilteredProducts());

        // Spinner 설정
        setupDeviceTypeSpinner();

        // 제조사 Grid 설정
        setupManufacturerGrid();

        // 제품명 검색창 TextWatcher 추가
        productNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleFiltersBasedOnProductName(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Spinner, RadioGroup, 가격 필터에 대한 이벤트 추가
        setupFilterListeners();
    }

    private void setupDeviceTypeSpinner() {
        // Spinner 데이터 설정
        String[] deviceTypes = getResources().getStringArray(R.array.device_types);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                deviceTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceTypeSpinner.setAdapter(adapter);

        // Spinner 선택 리스너
        deviceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // None 선택 여부에 따라 조건 활성화
                if (position == 0) {
                    selectedDeviceType = "";
                } else {
                    selectedDeviceType = parent.getItemAtPosition(position).toString();
                }
                toggleProductNameBasedOnFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void setupManufacturerGrid() {
        for (int i = 0; i < manufacturerGrid.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) manufacturerGrid.getChildAt(i);
            int index = i + 1; // 1부터 시작 (Samsung: 1, Apple: 2, ...)

            radioButton.setOnClickListener(v -> {
                if (selectedManufacturerIndex == index) {
                    // 이미 선택된 상태에서 다시 클릭 시 미선택으로 변경
                    radioButton.setChecked(false);
                    selectedManufacturerIndex = -1;
                } else {
                    // 선택된 제조사를 변경
                    selectedManufacturerIndex = index;
                    clearOtherRadioButtons(index);
                }
            });
        }
    }

    // 다른 라디오 버튼들의 선택을 해제
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
        // 최소 가격 EditText
        minPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleProductNameBasedOnFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 최대 가격 EditText
        maxPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleProductNameBasedOnFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void toggleFiltersBasedOnProductName(boolean isProductNameEntered) {
        // 제품명 검색창 입력 여부에 따라 나머지 필터 비활성화
        isFilterConditionActive = isProductNameEntered;

        deviceTypeSpinner.setEnabled(!isProductNameEntered);
        minPriceEditText.setEnabled(!isProductNameEntered);
        maxPriceEditText.setEnabled(!isProductNameEntered);

        for (int i = 0; i < manufacturerGrid.getChildCount(); i++) {
            View child = manufacturerGrid.getChildAt(i);
            child.setEnabled(!isProductNameEntered);
        }
    }

    private void toggleProductNameBasedOnFilters() {
        // 필터 조건이 하나라도 활성화되면 제품명 검색창 비활성화
        boolean isAnyFilterActive = deviceTypeSpinner.getSelectedItemPosition() != 0 ||
                !minPriceEditText.getText().toString().trim().isEmpty() ||
                !maxPriceEditText.getText().toString().trim().isEmpty();

        for (int i = 0; i < manufacturerGrid.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) manufacturerGrid.getChildAt(i);
            if (radioButton.isChecked()) {
                isAnyFilterActive = true;
                break;
            }
        }

        productNameEditText.setEnabled(!isAnyFilterActive);
    }

    private void fetchFilteredProducts() {
        // Firebase Authentication을 통해 현재 사용자 UID 가져오기
        String userId;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            // 로그인하지 않은 상태 처리
            Toast.makeText(this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 입력값 가져오기
        String productName = productNameEditText.getText().toString();
        int minPrice = minPriceEditText.getText().toString().isEmpty() ? -1 : Integer.parseInt(minPriceEditText.getText().toString());
        int maxPrice = maxPriceEditText.getText().toString().isEmpty() ? -1 : Integer.parseInt(maxPriceEditText.getText().toString());
        int deviceType = deviceTypeSpinner.getSelectedItemPosition(); // None: -1, laptops: 1, phones: 2, tablets: 3
        int manufacturer = selectedManufacturerIndex;

        // SearchData 객체 생성
        SearchData searchData = new SearchData(
                deviceType == 0 ? -1 : deviceType, // Spinner의 선택값 (-1이면 선택 안 함)
                productName.isEmpty() ? null : productName, // 제품명 없으면 null
                minPrice,
                maxPrice,
                manufacturer
        );

        // Intent에 SearchData 추가
        Intent intent = new Intent(this, InventoryProductPage.class);
        intent.putExtra("searchData", searchData);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

}