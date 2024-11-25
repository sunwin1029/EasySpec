package com.example.easyspec.Firebase;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.Data.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Firebase와 상호작용하기 위한 헬퍼 클래스
public class FirebaseHelper {

    // FirebaseHelper의 싱글톤 인스턴스
    private static FirebaseHelper instance;
    private List<ProductItem> productItems = new ArrayList<>(); // 불러온 제품 리스트를 저장하는 리스트
    private DatabaseReference databaseReference; // Firebase Realtime Database의 "ProductItems" 참조
    private DatabaseReference userReference; // Firebase Realtime Database의 "Users" 참조

    // 생성자: Firebase Realtime Database의 "ProductItems"와 "Users" 경로 참조 생성
    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ProductItems");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    // 싱글톤 패턴 구현: FirebaseHelper의 단일 인스턴스를 반환
    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    /**
     * Firebase에서 데이터를 한 번만 불러오는 메서드
     * Firebase Realtime Database에서 데이터를 한 번만 가져오고, 데이터를 처리한 후 콜백을 호출합니다.
     *
     * @param callback 데이터 로드 완료 또는 실패 시 호출될 콜백 인터페이스
     */
    public void fetchAllDataFromFirebase(final FirebaseCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productItems.clear(); // 기존 데이터를 지워 새로 로드

                // Firebase의 전체 데이터를 재귀적으로 탐색하여 제품 정보를 추출
                traverseSnapshot(snapshot);

                // 데이터 로드 성공 시 콜백 호출
                callback.onSuccess(productItems);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // 데이터 로드 실패 시 콜백 호출
                callback.onFailure(error.toException());
            }
        });
    }

    /**
     * Firebase의 데이터를 재귀적으로 탐색하여 제품 정보를 추출하는 메서드
     * 주어진 DataSnapshot 객체에서 자식 데이터를 순차적으로 탐색하며,
     * 각 제품 정보를 추출하여 리스트에 저장합니다.
     *
     * @param snapshot Firebase DataSnapshot 객체
     */
    private void traverseSnapshot(DataSnapshot snapshot) {
        for (DataSnapshot child : snapshot.getChildren()) {
            // 각 제품이 'name', 'price', 'productType' 속성을 갖고 있는지 확인
            if (child.hasChild("name") && child.hasChild("price") && child.hasChild("productType")) {
                // 제품 정보를 추출
                String name = child.child("name").getValue(String.class);
                int price = child.child("price").getValue(Integer.class);
                int productType = child.child("productType").getValue(Integer.class);

                // 선택적 필드 'rating'과 'heart'를 처리
                double rating = child.child("rating").getValue(Double.class) != null ?
                        child.child("rating").getValue(Double.class) : 0.0;  // 기본값 0.0
                boolean heart = child.child("heart").getValue(Boolean.class) != null &&
                        child.child("heart").getValue(Boolean.class);

                // ProductItem 객체 생성 후 리스트에 추가
                ProductItem item = new ProductItem(name, price, 0, rating, heart, productType, null);
                productItems.add(item);
            }

            // 자식 데이터가 더 있을 경우 재귀 호출하여 추가 탐색
            if (child.getChildrenCount() > 0) {
                traverseSnapshot(child);
            }
        }
    }

    /**
     * Firebase에서 특정 사용자의 favorite_item 목록에 아이템 추가
     * @param userId 사용자 ID
     * @param productName 추가할 상품 이름
     * @param callback 작업 성공/실패 시 호출될 콜백
     */
    public void addFavoriteItem(String userId, String productName, final FirebaseCallback callback) {
        DatabaseReference favoriteRef = userReference.child(userId).child("favorite_item");

        // favorite_item 데이터 불러오기
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Boolean> favoriteItems = new HashMap<>();

                // 기존 favorite_item이 있는 경우
                if (snapshot.exists()) {
                    favoriteItems = (Map<String, Boolean>) snapshot.getValue();
                }

                // 새 상품 추가
                favoriteItems.put(productName, true);

                // 업데이트 작업
                favoriteRef.setValue(favoriteItems, (error, ref) -> {
                    if (error == null) {
                        callback.onSuccess(null); // 성공 시 콜백
                    } else {
                        callback.onFailure(error.toException()); // 실패 시 콜백
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    /**
     * Firebase에서 특정 사용자의 favorite_item 목록에서 아이템 제거
     * @param userId 사용자 ID
     * @param productName 제거할 상품 이름
     * @param callback 작업 성공/실패 시 호출될 콜백
     */
    public void removeFavoriteItem(String userId, String productName, final FirebaseCallback callback) {
        DatabaseReference favoriteRef = userReference.child(userId).child("favorite_item");

        // favorite_item 데이터 불러오기
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Boolean> favoriteItems = (Map<String, Boolean>) snapshot.getValue();

                    // 상품 제거
                    if (favoriteItems != null && favoriteItems.containsKey(productName)) {
                        favoriteItems.remove(productName);

                        // 업데이트 작업
                        favoriteRef.setValue(favoriteItems, (error, ref) -> {
                            if (error == null) {
                                callback.onSuccess(null); // 성공 시 콜백
                            } else {
                                callback.onFailure(error.toException()); // 실패 시 콜백
                            }
                        });
                    } else {
                        callback.onFailure(new Exception("Item not found in favorite_item."));
                    }
                } else {
                    callback.onFailure(new Exception("favorite_item field does not exist."));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    /**
     * Firebase에서 로드된 제품 리스트를 반환
     * @return 제품 리스트
     */
    public List<ProductItem> getProductItems() {
        return productItems;
    }

    /**
     * Firebase 작업 결과를 전달하는 콜백 인터페이스
     */
    public interface FirebaseCallback {
        void onSuccess(List<ProductItem> productItems); // 작업 성공 시 호출
        void onFailure(Exception e); // 작업 실패 시 호출
    }



    /**
     * Firebase에서 사용자 데이터를 가져오는 메서드
     * @param callback 데이터를 가져온 후 호출될 콜백
     */
    public void fetchAllUsersFromFirebase(final FirebaseUserCallback callback) {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Users> userList = new ArrayList<>(); // 사용자 데이터를 저장할 리스트

                // 모든 사용자 데이터를 탐색하여 객체로 변환
                for (DataSnapshot child : snapshot.getChildren()) {
                    Users user = child.getValue(Users.class);
                    if (user != null) {
                        userList.add(user); // 리스트에 추가
                    }
                }

                // 성공적으로 데이터를 가져왔을 때 콜백 호출
                callback.onSuccess(userList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // 데이터를 가져오지 못했을 때 콜백 호출
                callback.onFailure(error.toException());
            }
        });
    }

    /**
     * Firebase 작업 결과를 전달하는 사용자 콜백 인터페이스
     */
    public interface FirebaseUserCallback {
        void onSuccess(List<Users> users); // 작업 성공 시 호출
        void onFailure(Exception e); // 작업 실패 시 호출
    }
}