package com.example.easyspec;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeviceChangeFragment extends DialogFragment {
    private Spinner spinnerLaptop, spinnerTablet, spinnerPhone;
    private Button buttonSave;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_devices, container, false);

        spinnerLaptop = view.findViewById(R.id.spinnerLaptop);
        spinnerTablet = view.findViewById(R.id.spinnerTablet);
        spinnerPhone = view.findViewById(R.id.spinnerPhone);
        buttonSave = view.findViewById(R.id.saveButton);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // 스피너 설정
        ArrayAdapter<CharSequence> laptopAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.laptop_options, android.R.layout.simple_spinner_item);
        laptopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLaptop.setAdapter(laptopAdapter);

        ArrayAdapter<CharSequence> tabletAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.tablet_options, android.R.layout.simple_spinner_item);
        tabletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTablet.setAdapter(tabletAdapter);

        ArrayAdapter<CharSequence> phoneAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.phone_options, android.R.layout.simple_spinner_item);
        phoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhone.setAdapter(phoneAdapter);

        // Firebase에서 데이터 불러오기
        loadDeviceInfo();

        buttonSave.setOnClickListener(v -> saveDeviceInfo());

        return view;
    }

    private void loadDeviceInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String laptop = dataSnapshot.child("laptop").getValue(String.class);
                    String tablet = dataSnapshot.child("tablet").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);

                    // 불러온 데이터로 스피너 값 설정
                    setSpinnerValue(spinnerLaptop, laptop);
                    setSpinnerValue(spinnerTablet, tablet);
                    setSpinnerValue(spinnerPhone, phone);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }


    private void saveDeviceInfo() {
        String laptop = spinnerLaptop.getSelectedItem().toString();
        String tablet = spinnerTablet.getSelectedItem().toString();
        String phone = spinnerPhone.getSelectedItem().toString();

        userRef.child("laptop").setValue(laptop);
        userRef.child("tablet").setValue(tablet);
        userRef.child("phone").setValue(phone).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "디바이스 정보가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                dismiss(); // 프래그먼트 닫기
            } else {
                Toast.makeText(getContext(), "디바이스 변경 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
