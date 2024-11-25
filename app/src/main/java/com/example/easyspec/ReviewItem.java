package com.example.easyspec;

public class ReviewItem {
    private String batteryReview;
    private String performanceReview;
    private String key; // Firebase key

    public ReviewItem() {
        // Default constructor required for Firebase
    }

    public ReviewItem(String userId, String batteryReview, String performanceReview) {
    }

    public String getBatteryReview() {
        return batteryReview;
    }

    public void setBatteryReview(String batteryReview) {
        this.batteryReview = batteryReview;
    }

    public String getPerformanceReview() {
        return performanceReview;
    }

    public void setPerformanceReview(String performanceReview) {
        this.performanceReview = performanceReview;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
