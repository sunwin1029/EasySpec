package com.example.easyspec.Data;

public class ProductItem{
    private String name; // 이름, 불변함
    private int price; // 가격, 불변함
    private int imageResource; // 이미지 파일, 불변함
    private double rating; // 평점
    private boolean heart; // 즐겨찾기
    private int productType; // 1 -> 노트북 2 -> 태블릿 3 -> 핸드폰
    private UserOfTheProduct usersOfTheProduct;

    public String getName() {
        return name;
    }

    public int getProductType() {return productType;}

    public int getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public boolean heart() {
        return heart;
    }

    public double getRating() { return rating; }



    public ProductItem(String name, int price, int imageResource, double rating, boolean heart, int productType, UserOfTheProduct user) {
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
        this.rating = rating;
        this.heart = heart;
        this.productType = productType;
        this.usersOfTheProduct = user;
    }

    //public void addRating(String userID, int
}




