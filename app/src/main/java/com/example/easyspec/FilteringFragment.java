package com.example.easyspec;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easyspec.Data.SearchData;

public class FilteringFragment extends Fragment {

    private static final String TAG = "Search_Filter";
    private Spinner deviceTypeSpinner;
    private EditText productNameEditText, minPriceEditText, maxPriceEditText;
    private GridLayout manufacturerGrid;
    private String selectedDeviceType = "";
    private boolean isFilterConditionActive = false;
    private int selectedManufacturerIndex = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filtering, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UI 요소 초기화
        deviceTypeSpinner = view.findViewById(R.id.device_type_spinner);
        productNameEditText = view.findViewById(R.id.productNameEditText);
        minPriceEditText = view.findViewById(R.id.min_price);
        maxPriceEditText = view.findViewById(R.id.max_price);
        manufacturerGrid = view.findViewById(R.id.manufacturer_radio_group);

        view.findViewById(R.id.search_button).setOnClickListener(v -> fetchFilteredProducts());
        view.findViewById(R.id.back_button).setOnClickListener(v -> navigateToMainScreen());

        // 초기화 메서드 호출
        setupDeviceTypeSpinner();
        setupManufacturerGrid();
        setupFilterListeners();

        // 검색창 텍스트 변경 이벤트 설정
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
    }

    private void setupDeviceTypeSpinner() {
        String[] deviceTypes = getResources().getStringArray(R.array.device_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, deviceTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceTypeSpinner.setAdapter(adapter);

        deviceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeviceType = (position == 0) ? "" : parent.getItemAtPosition(position).toString();
                toggleProductNameBasedOnFilters();
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
            if (i + 1 != exceptIndex) {
                radioButton.setChecked(false);
            }
        }
    }

    private void setupFilterListeners() {
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
        String productName = productNameEditText.getText().toString();
        int minPrice = minPriceEditText.getText().toString().isEmpty() ? -1 : Integer.parseInt(minPriceEditText.getText().toString());
        int maxPrice = maxPriceEditText.getText().toString().isEmpty() ? -1 : Integer.parseInt(maxPriceEditText.getText().toString());
        int deviceType = deviceTypeSpinner.getSelectedItemPosition();
        int manufacturer = selectedManufacturerIndex;

        SearchData searchData = new SearchData(
                deviceType == 0 ? -1 : deviceType,
                productName.isEmpty() ? null : productName,
                minPrice,
                maxPrice,
                manufacturer
        );

        Log.d(TAG, "SearchData: " + searchData.toString());
    }

    private void navigateToMainScreen() {
        if (getActivity() != null) {
            // 현재 프래그먼트를 스택에서 제거하고 Activity를 종료
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            Log.e(TAG, "Activity가 null 상태입니다. Navigation 실패.");
        }
    }
}
