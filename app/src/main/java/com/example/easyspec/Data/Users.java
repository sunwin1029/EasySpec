package com.example.easyspec.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Users implements Serializable {
    private String email;
    private String university;
    private String laptop;
    private String tablet;
    private String phone;

    // 즐겨찾기한 상품 ID를 저장하는 필드 추가
    private Set<String> favoriteProductIds;

    public Users() {
        favoriteProductIds = new HashSet<>(); // 초기화
    }

    public Users(String email, String university, String laptop, String tablet, String phone) {
        this.email = email;
        this.university = university;
        this.laptop = laptop;
        this.tablet = tablet;
        this.phone = phone;
        this.favoriteProductIds = new HashSet<>();
    }

    // Getter & Setter
    public String getEmail() {
        return email;
    }

    public String getUniversity() {
        return university;
    }

    public String getLaptop() {
        return laptop;
    }

    public String getTablet() {
        return tablet;
    }

    public String getPhone() {
        return phone;
    }

    public Set<String> getFavoriteProductIds() {
        return favoriteProductIds;
    }

    public void addFavoriteProduct(String productId) {
        favoriteProductIds.add(productId);
    }

    public void removeFavoriteProduct(String productId) {
        favoriteProductIds.remove(productId);
    }

    public boolean isFavorite(String productId) {
        return favoriteProductIds.contains(productId);
    }
}