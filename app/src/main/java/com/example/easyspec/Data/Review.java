package com.example.easyspec.Data;

import java.io.Serializable;

public class Review implements Serializable {

    private String reviewText; // 리뷰 내용
    private String userId;     // 작성자 ID
    private int likes;         // 좋아요 개수

    // 생성자
    public Review(String reviewText, String userId, int likes) {
        this.reviewText = reviewText;
        this.userId = userId;
        this.likes = likes;
    }

    // Getter 메서드
    public String getReviewText() {
        return reviewText;
    }

    public String getUserId() {
        return userId;
    }

    public int getLikes() {
        return likes;
    }

    // Setter 메서드 (필요에 따라 추가 가능)
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    // 추가적인 유틸리티 메서드
    @Override
    public String toString() {
        return "Review{" +
                "reviewText='" + reviewText + '\'' +
                ", userId='" + userId + '\'' +
                ", likes=" + likes +
                '}';
    }
}
