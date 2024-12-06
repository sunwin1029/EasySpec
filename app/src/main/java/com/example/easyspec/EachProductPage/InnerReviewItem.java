package com.example.easyspec.EachProductPage;

public class InnerReviewItem {
    private String department;
    private String reviewText;
    private int goodCount;

    public InnerReviewItem(String department, String reviewText, int goodCount) {
        this.department = department;
        this.reviewText = reviewText;
        this.goodCount = goodCount;
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
}
