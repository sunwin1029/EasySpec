package com.example.easyspec.LogIn;

import android.os.Bundle;
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
    private Spinner spinnerLaptop, spinnerTablet, spinnerPhone;
    private Button buttonSave;
    private DatabaseReference productDatabase;
    private DatabaseReference userRef;
    private List<String> laptopList = new ArrayList<>();
    private List<String> tabletList = new ArrayList<>();
    private List<String> phoneList = new ArrayList<>();
    private String university; // 대학교 정보를 저장할 변수
    private String currentLaptop, currentTablet, currentPhone; // 현재 선택된 기기 정보 저장

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
            university = getArguments().getString("university");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_devices, container, false);

        spinnerLaptop = view.findViewById(R.id.spinnerLaptop);
        spinnerTablet = view.findViewById(R.id.spinnerTablet);
        spinnerPhone = view.findViewById(R.id.spinnerPhone);
        buttonSave = view.findViewById(R.id.saveButton);

        productDatabase = FirebaseDatabase.getInstance().getReference("ProductItems");
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Load product data
        loadProductData();

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadProductData() {
        productDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                laptopList.clear();
                tabletList.clear();
                phoneList.clear();

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

                setSpinnerData(spinnerLaptop, laptopList);
                setSpinnerData(spinnerTablet, tabletList);
                setSpinnerData(spinnerPhone, phoneList);

                // Load current user device information
                loadCurrentDeviceInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentDeviceInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentLaptop = dataSnapshot.child("laptop").getValue(String.class);
                    currentTablet = dataSnapshot.child("tablet").getValue(String.class);
                    currentPhone = dataSnapshot.child("phone").getValue(String.class);

                    // Set spinner values based on current device info
                    setSpinnerValue(spinnerLaptop, currentLaptop);
                    setSpinnerValue(spinnerTablet, currentTablet);
                    setSpinnerValue(spinnerPhone, currentPhone);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "현재 기기 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerData(Spinner spinner, List<String> dataList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setSpinnerValue(Spinner spinner, String value) {
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
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), "사용자 수 업데이트 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // newDevice에서 UserNum을 +1
        if (newDevice != null) {
            productDatabase.orderByChild("name").equalTo(newDevice).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        DatabaseReference userNumRef = productSnapshot.child("UserNum").child(university).getRef();
                        userNumRef.setValue(ServerValue.increment(1)); // 사용자 수 +1
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), "사용자 수 업데이트 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveDeviceInfo() {
        String laptop = spinnerLaptop.getSelectedItem().toString();
        String tablet = spinnerTablet.getSelectedItem().toString();
        String phone = spinnerPhone.getSelectedItem().toString();

        // Update user's device info in the database
        userRef.child("laptop").setValue(laptop);
        userRef.child("tablet").setValue(tablet);
        userRef.child("phone").setValue(phone).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "기기 정보가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                dismiss(); // 닫기
            } else {
                Toast.makeText(getContext(), "기기 정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
