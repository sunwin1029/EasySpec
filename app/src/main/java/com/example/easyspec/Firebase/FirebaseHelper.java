package com.example.easyspec.Firebase;

import com.example.easyspec.Data.ProductItem;
import com.example.easyspec.ReviewItem; // 리뷰 데이터를 담을 ReviewItem 클래스를 추가
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Firebase와 관련된 데이터 접근 및 조작을 담당하는 헬퍼 클래스
 */
public class FirebaseHelper {

    private static FirebaseHelper instance; // Singleton 패턴 구현을 위한 static 변수
    private List<ProductItem> productItems = new ArrayList<>(); // Firebase에서 가져온 ProductItem 데이터를 저장할 리스트
    private DatabaseReference databaseReference; // Firebase Database 참조

    /**
     * FirebaseHelper 생성자: "ProductItems" 경로를 기본으로 설정
     */
    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("ProductItems");
    }

    /**
     * Singleton 인스턴스 반환
     */
    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    /**
     * Firebase에서 모든 ProductItem 데이터를 비동기로 가져오는 메서드
     *
     * @param callback 작업 성공/실패 결과를 반환받을 콜백 인터페이스
     */
    public void fetchAllDataFromFirebase(final FirebaseCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productItems.clear(); // 기존 데이터를 비움
                traverseSnapshot(snapshot); // Firebase 데이터 구조를 순회하며 파싱
                callback.onSuccess(productItems); // 성공적으로 데이터를 반환
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException()); // 실패 시 에러 반환
            }
        });
    }

    /**
     * Firebase 스냅샷을 재귀적으로 순회하며 ProductItem 객체를 생성
     *
     * @param snapshot Firebase 데이터 스냅샷
     */
    private void traverseSnapshot(DataSnapshot snapshot) {
        for (DataSnapshot child : snapshot.getChildren()) {
            // 필요한 필드를 모두 가진 데이터만 처리
            if (child.hasChild("name") && child.hasChild("price") && child.hasChild("productType")) {
                String name = child.child("name").getValue(String.class);
                int price = child.child("price").getValue(Integer.class);
                int productType = child.child("productType").getValue(Integer.class);
                double rating = child.child("rating").getValue(Double.class) != null ?
                        child.child("rating").getValue(Double.class) : 0.0;
                boolean heart = child.child("heart").getValue(Boolean.class) != null &&
                        child.child("heart").getValue(Boolean.class);

                // ProductItem 객체 생성 및 리스트에 추가
                ProductItem item = new ProductItem(name, price, 0, rating, heart, productType, null);
                productItems.add(item);
            }

            // 하위 노드가 있는 경우 재귀 호출
            if (child.getChildrenCount() > 0) {
                traverseSnapshot(child);
            }
        }
    }

    /**
     * 특정 제품 데이터를 Firebase에서 수정하는 메서드
     *
     * @param category   수정할 제품이 속한 카테고리(laptops, phones, tablets)
     * @param productId  수정할 제품의 ID(product1, product2 등)
     * @param updatedItem 업데이트할 데이터를 포함하는 ProductItem 객체
     * @param callback   작업 성공/실패 결과를 반환받을 콜백 인터페이스
     */
    public void updateProduct(String category, String productId, ProductItem updatedItem, final FirebaseCallback callback) {
        DatabaseReference productRef = databaseReference.child(category).child(productId);

        // 각 필드를 Firebase에 업데이트
        productRef.child("name").setValue(updatedItem.getName());
        productRef.child("price").setValue(updatedItem.getPrice());
        productRef.child("productType").setValue(updatedItem.getProductType());
        productRef.child("rating").setValue(updatedItem.getRating());
        productRef.child("heart").setValue(updatedItem.heart());

        // 전체 데이터를 업데이트하고 결과를 콜백으로 반환
        productRef.setValue(updatedItem, (error, ref) -> {
            if (error == null) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(error.toException());
            }
        });
    }

    /**
     * 현재 Firebase에 로드된 ProductItem 리스트를 반환
     */
    public List<ProductItem> getProductItems() {
        return productItems;
    }

    /**
     * 특정 제품에 대한 리뷰를 Firebase에서 가져오는 메서드
     *
     * @param productId 리뷰를 가져올 제품 ID (예: product1, product2)
     * @param callback  작업 성공/실패 결과를 반환받을 콜백 인터페이스
     */
    public void getReviewsForProduct(String productId, final ReviewCallback callback) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews").child(productId);

        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ReviewItem> reviews = new ArrayList<>();
                // 리뷰 데이터를 순회하며 ReviewItem 객체 생성
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String batteryReview = userSnapshot.child("batteryReview").getValue(String.class);
                    String performanceReview = userSnapshot.child("performanceReview").getValue(String.class);

                    ReviewItem review = new ReviewItem(userId, batteryReview, performanceReview);
                    reviews.add(review);
                }
                callback.onSuccess(reviews); // 성공적으로 리뷰 데이터를 반환
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException()); // 실패 시 에러 반환
            }
        });
    }

    /**
     * 특정 사용자의 모든 리뷰를 Firebase에서 가져오는 메서드
     *
     * @param userId   리뷰를 가져올 사용자 ID
     * @param callback 작업 성공/실패 결과를 반환받을 콜백 인터페이스
     */
    public void getUserReviews(String userId, final ReviewCallback callback) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ReviewItem> reviews = new ArrayList<>();
                // 모든 제품의 리뷰를 순회하며 해당 사용자의 리뷰만 가져옴
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot userReviewSnapshot = productSnapshot.child(userId);
                    if (userReviewSnapshot.exists()) {
                        String productId = productSnapshot.getKey();
                        String batteryReview = userReviewSnapshot.child("batteryReview").getValue(String.class);
                        String performanceReview = userReviewSnapshot.child("performanceReview").getValue(String.class);

                        ReviewItem review = new ReviewItem();
                        review.setKey(productId);
                        review.setBatteryReview(batteryReview);
                        review.setPerformanceReview(performanceReview);

                        reviews.add(review);
                    }
                }
                callback.onSuccess(reviews); // 성공적으로 리뷰 데이터를 반환
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.toException()); // 실패 시 에러 반환
            }
        });
    }

    /**
     * Firebase에서 현재 로그인된 사용자의 ID를 가져오는 메서드
     */
    public static String getCurrentUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null; // 로그인된 사용자가 없으면 null 반환
    }

    /**
     * Firebase에서 특정 리뷰를 수정하는 메서드
     *
     * @param productId              수정할 제품 ID
     * @param userId                 리뷰 작성자 ID
     * @param updatedBatteryReview   수정할 배터리 리뷰
     * @param updatedPerformanceReview 수정할 성능 리뷰
     * @param callback               작업 성공/실패 결과를 반환받을 콜백 인터페이스
     */
    public void updateReview(String productId, String userId,
                             String updatedBatteryReview, String updatedPerformanceReview,
                             final FirebaseCallback callback) {
        DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                .getReference("reviews")
                .child(productId)
                .child(userId);

        // 업데이트할 데이터를 Map 형태로 생성
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("batteryReview", updatedBatteryReview);
        updatedData.put("performanceReview", updatedPerformanceReview);

        // Firebase에 업데이트 요청
        reviewRef.updateChildren(updatedData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null); // 성공 시 콜백 호출
                    } else {
                        callback.onFailure(task.getException()); // 실패 시 콜백 호출
                    }
                });
    }

    /**
     * Firebase에서 데이터를 처리하기 위한 콜백 인터페이스
     */
    public interface FirebaseCallback {
        void onSuccess(List<ProductItem> productItems);
        void onFailure(Exception e);
    }

    /**
     * 리뷰 데이터를 처리하기 위한 콜백 인터페이스
     */
    public interface ReviewCallback {
        void onSuccess(List<ReviewItem> reviews); // 작업 성공 시 호출
        void onFailure(Exception e); // 작업 실패 시 호출
    }
}
