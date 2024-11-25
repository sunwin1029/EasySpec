package com.example.easyspec.EachProductPage;

public class ReviewInEachProductProperty {
    private String userName; // 사용자 이름
    private String reviewText; // 리뷰 내용
    private int reviewLikes; // 좋아요 수

    public ReviewInEachProductProperty(String userName, String reviewText, int reviewLikes) {
        this.userName = userName;
        this.reviewText = reviewText;
        this.reviewLikes = reviewLikes;
    }

    public String getUserName() {
        return userName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public int getReviewLikes() {
        return reviewLikes;
    }

}