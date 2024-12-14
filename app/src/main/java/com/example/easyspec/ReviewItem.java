package com.example.easyspec;

public class ReviewItem {
    private String key;          // 리뷰 고유 키
    private String userId;      // 리뷰 작성자 ID
    private String productId;   // 리뷰가 작성된 제품 ID
    private String reviewText;  // 리뷰 내용
    private int likes;       // 리뷰 점수
    private int imageResource;  // 제품 이미지 리소스 ID
    private String name;  // 제품 이름
    private String feature;      // 리뷰의 특징 (예: 카메라, 배터리 등)

    // 기본 생성자
    public ReviewItem() {
    }

    // 모든 필드를 초기화하는 생성자
    public ReviewItem(String key, String userId, String productId, String reviewText, int likes, int imageResource, String name, String feature) {
        this.key = key; // 리뷰 고유 키 초기화
        this.userId = userId;
        this.productId = productId;
        this.reviewText = reviewText;
        this.likes = likes;
        this.imageResource = imageResource;
        this.name = name;
        this.feature = feature; // 특징 초기화
    }

    // Getter 메서드들
    public String getKey() {
        return key; // 고유 키 반환
    }

    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public int getlikes() {
        return likes;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name; // 제품 이름 반환
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getFeature() { // 특징을 반환하는 메서드 추가
        return feature;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
