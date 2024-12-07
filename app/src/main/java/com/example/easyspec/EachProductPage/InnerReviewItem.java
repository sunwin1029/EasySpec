package com.example.easyspec.EachProductPage;

public class InnerReviewItem {
    private String department;
    private String reviewText;
    private int goodCount;
    private String reviewId; // reviewId 필드 추가

    public InnerReviewItem(String department, String reviewText, int goodCount, String reviewId) {
        this.department = department;
        this.reviewText = reviewText;
        this.goodCount = goodCount;
        this.reviewId = reviewId; // 필드 초기화
    }

    public String getDepartment() {
        return department;
    }

    public String getReviewText() {
        return reviewText;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public void incrementGoodCount() {
        this.goodCount++;
    }

    public void decrementGoodCount() {
        this.goodCount--;
    }

    public String getReviewId() {
        return reviewId;
    }
}
