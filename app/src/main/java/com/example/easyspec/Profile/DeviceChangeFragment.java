package com.example.easyspec.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.easyspec.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeviceChangeFragment extends DialogFragment {
    private Spinner spinnerLaptop, spinnerTablet, spinnerPhone; // 기기 선택 스피너
    private Button buttonSave; // 저장 버튼
    private DatabaseReference productDatabase; // 제품 데이터베이스 참조
    private DatabaseReference userRef; // 사용자 데이터베이스 참조
    private List<String> laptopList = new ArrayList<>(); // 노트북 리스트
    private List<String> tabletList = new ArrayList<>(); // 태블릿 리스트
    private List<String> phoneList = new ArrayList<>(); // 핸드폰 리스트
    private String university; // 대학교 정보를 저장할 변수
    private String currentLaptop, currentTablet, currentPhone; // 현재 선택된 기기 정보 저장

    // DeviceChangeFragment의 새 인스턴스 생성
    public static DeviceChangeFragment newInstance(String university) {
        DeviceChangeFragment fragment = new DeviceChangeFragment();
        Bundle args = new Bundle();
        args.putString("university", university);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            university = getArguments().getString("university"); // 대학교 정보 가져오기
            Log.d("EasySpec", "University info: " + university); // 대학교 정보 로그
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_devices, container, false); // 레이아웃 설정

        // UI 요소 초기화
        spinnerLaptop = view.findViewById(R.id.spinnerLaptop);
        spinnerTablet = view.findViewById(R.id.spinnerTablet);
        spinnerPhone = view.findViewById(R.id.spinnerPhone);
        buttonSave = view.findViewById(R.id.saveButton);

        productDatabase = FirebaseDatabase.getInstance().getReference("ProductItems"); // 제품 데이터베이스 참조 초기화
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()); // 현재 사용자 데이터베이스 참조 초기화

        // 제품 데이터 로드
        loadProductData();

        // 저장 버튼 클릭 시 기기 정보 저장
        buttonSave.setOnClickListener(v -> saveDeviceInfo());

        // 스피너의 선택 변경 리스너 설정
        setupSpinnerListeners();

        return view;
    }

    private void setupSpinnerListeners() {
        // 노트북 선택 리스너
        spinnerLaptop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newLaptop = spinnerLaptop.getSelectedItem().toString();
                if (currentLaptop != null && !currentLaptop.equals(newLaptop)) {
                    updateUserNum(currentLaptop, newLaptop); // 사용자 수 업데이트
                }
                currentLaptop = newLaptop; // 새 기기로 업데이트
                Log.d("EasySpec", "Laptop selected: " + newLaptop); // 선택된 노트북 로그
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 태블릿 선택 리스너
        spinnerTablet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newTablet = spinnerTablet.getSelectedItem().toString();
                if (currentTablet != null && !currentTablet.equals(newTablet)) {
                    updateUserNum(currentTablet, newTablet); // 사용자 수 업데이트
                }
                currentTablet = newTablet; // 새 기기로 업데이트
                Log.d("EasySpec", "Tablet selected: " + newTablet); // 선택된 태블릿 로그
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 핸드폰 선택 리스너
        spinnerPhone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newPhone = spinnerPhone.getSelectedItem().toString();
                if (currentPhone != null && !currentPhone.equals(newPhone)) {
                    updateUserNum(currentPhone, newPhone); // 사용자 수 업데이트
                }
                currentPhone = newPhone; // 새 기기로 업데이트
                Log.d("EasySpec", "Phone selected: " + newPhone); // 선택된 핸드폰 로그
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadProductData() {
        // 제품 데이터 로드
        productDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                laptopList.clear();
                tabletList.clear();
                phoneList.clear();

                // 데이터 스냅샷을 순회하며 각 기기 타입에 따라 리스트에 추가
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String name = productSnapshot.child("name").getValue(String.class);
                    int productType = productSnapshot.child("productType").getValue(Integer.class);

                    switch (productType) {
                        case 1:
                            laptopList.add(name);
                            break;
                        case 2:
                            tabletList.add(name);
                            break;
                        case 3:
                            phoneList.add(name);
                            break;
                    }
                }

                // 스피너 데이터 설정
                setSpinnerData(spinnerLaptop, laptopList);
                setSpinnerData(spinnerTablet, tabletList);
                setSpinnerData(spinnerPhone, phoneList);

                // 현재 사용자 기기 정보 로드
                loadCurrentDeviceInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
                Log.e("EasySpec", "Data load failed: " + databaseError.getMessage()); // 데이터 로드 실패 로그
            }
        });
    }

    private void loadCurrentDeviceInfo() {
        // 현재 사용자 기기 정보 로드
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentLaptop = dataSnapshot.child("laptop").getValue(String.class);
                    currentTablet = dataSnapshot.child("tablet").getValue(String.class);
                    currentPhone = dataSnapshot.child("phone").getValue(String.class);

                    // 스피너 값을 현재 기기 정보에 따라 설정
                    setSpinnerValue(spinnerLaptop, currentLaptop);
                    setSpinnerValue(spinnerTablet, currentTablet);
                    setSpinnerValue(spinnerPhone, currentPhone);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load current device info.", Toast.LENGTH_SHORT).show();
                Log.e("EasySpec", "Failed to load current device info: " + databaseError.getMessage()); // 현재 기기 정보 로드 실패 로그
            }
        });
    }

    private void setSpinnerData(Spinner spinner, List<String> dataList) {
        // 스피너에 데이터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        // 스피너에 현재 값 설정
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    private void updateUserNum(String oldDevice, String newDevice) {
        // oldDevice에서 UserNum을 -1
        if (oldDevice != null) {
            productDatabase.orderByChild("name").equalTo(oldDevice).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        DatabaseReference userNumRef = productSnapshot.child("UserNum").child(university).getRef();
                        userNumRef.setValue(ServerValue.increment(-1)); // 사용자 수 -1
                        Log.d("EasySpec", oldDevice + "'s user count updated to -1"); // 기기 사용자 수 감소 로그
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("EasySpec", "Failed to update user count for " + oldDevice + ": " + databaseError.getMessage()); // 기기 사용자 수 업데이트 실패 로그
                }
            });
        }

        // newDevice에 UserNum을 +1
        if (newDevice != null) {
            productDatabase.orderByChild("name").equalTo(newDevice).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        DatabaseReference userNumRef = productSnapshot.child("UserNum").child(university).getRef();
                        userNumRef.setValue(ServerValue.increment(1)); // 사용자 수 +1
                        Log.d("EasySpec", newDevice + "'s user count updated to +1"); // 기기 사용자 수 증가 로그
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("EasySpec", "Failed to update user count for " + newDevice + ": " + databaseError.getMessage()); // 기기 사용자 수 업데이트 실패 로그
                }
            });
        }
    }

    private void saveDeviceInfo() {
        // 기기 정보 저장
        String laptop = spinnerLaptop.getSelectedItem().toString(); // 선택된 노트북 정보
        String tablet = spinnerTablet.getSelectedItem().toString(); // 선택된 태블릿 정보
        String phone = spinnerPhone.getSelectedItem().toString(); // 선택된 핸드폰 정보

        // 선택된 기기 정보를 저장
        userRef.child("laptop").setValue(laptop);
        Log.d("EasySpec", "Laptop info saved: " + laptop); // 노트북 정보 저장 로그

        userRef.child("tablet").setValue(tablet);
        Log.d("EasySpec", "Tablet info saved: " + tablet); // 태블릿 정보 저장 로그

        userRef.child("phone").setValue(phone);
        Log.d("EasySpec", "Phone info saved: " + phone); // 핸드폰 정보 저장 로그

        Toast.makeText(getContext(), "Device info saved successfully!", Toast.LENGTH_SHORT).show();
        dismiss(); // 다이얼로그 닫기
    }

}
