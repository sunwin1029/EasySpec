package com.example.easyspec.Data;

public class ProductItem {

    private String name; // 이름, 불변함
    private int price; // 가격, 불변함
    private int imageResource; // 이미지 파일, 불변함
    private double rating; // 평점
    private boolean heart; // 즐겨찾기 여부
    private int productType; // 1 -> 노트북 2 -> 태블릿 3 -> 핸드폰
    private UserOfTheProduct usersOfTheProduct;

    // 생성자
    public ProductItem(String name, int price, int imageResource, double rating, boolean heart, int productType, UserOfTheProduct user) {
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
        this.rating = rating;
        this.heart = heart;
        this.productType = productType;
        this.usersOfTheProduct = user;
    }

    // Getter 메서드들
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public double getRating() {
        return rating;
    }

    public boolean getHeart() {
        return heart;
    }

    public int getProductType() {
        return productType;
    }

    // 즐겨찾기 상태를 반환하는 메서드
    public boolean isFavorite() {
        return heart;
    }

    // 즐겨찾기 상태를 업데이트하는 메서드
    public void setFavorite(boolean isFavorite) {
        this.heart = isFavorite;
    }
}