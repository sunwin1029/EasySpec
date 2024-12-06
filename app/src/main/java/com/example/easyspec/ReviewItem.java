package com.example.easyspec;

public class ReviewItem {
    private String userId; // 리뷰 작성자 ID
    private String productId; // 리뷰가 작성된 제품 ID
    private String reviewText; // 리뷰 내용
    private int reviewScore; // 리뷰 점수
    private int imageResource; // 제품 이미지 리소스 ID
    private String productName; // 제품 이름

    // 기본 생성자 (Firebase에서 자동으로 객체 생성할 때 필요)
    public ReviewItem() {}

    // 모든 필드를 초기화하는 생성자
    public ReviewItem(String userId, String productId, String reviewText, int reviewScore, int imageResource, String productName) {
        this.userId = userId;
        this.productId = productId;
        this.reviewText = reviewText;
        this.reviewScore = reviewScore;
        this.imageResource = imageResource;
        this.productName = productName;
    }

    // Getter 메서드들
    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public int getReviewScore() {
        return reviewScore;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getProductName() {
        return productName;
    }
}
