package com.example.easyspec.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ProductItem implements Serializable {
    private String id;
    private String name;               // 제품 이름
    private int price;                 // 제품 가격
    private Integer imageResource;     // 이미지 리소스 (null 가능)
    private int productType;           // 1: 노트북, 2: 태블릿, 3: 핸드폰
    private String company;            // 제조사 정보
    private String features;           // 특징 설명
    private double totalRating;        // 총 평점 점수
    private int ratingCount;           // 평점 개수
    private boolean heart;             // 즐겨찾기 여부
    private Map<String, List<Review>> reviews;  // 리뷰: 카테고리별

    // 사용자 수 데이터 (기존 UserOfTheProduct 클래스 통합)
    private int IT;
    private int English;
    private int NaturalScience;
    private int EconomicsAndTrade;
    private int Law;
    private int SocialScience;

    // 생성자
    public ProductItem(String name, int price, Integer imageResource, int productType,
                       String company, String features, double totalRating, int ratingCount,
                       boolean heart, Map<String, List<Review>> reviews,
                       int IT, int English, int NaturalScience,
                       int EconomicsAndTrade, int Law, int SocialScience) {
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
        this.productType = productType;
        this.company = company;
        this.features = features;
        this.totalRating = totalRating;
        this.ratingCount = ratingCount;
        this.heart = heart;
        this.reviews = reviews;
        this.IT = IT;
        this.English = English;
        this.NaturalScience = NaturalScience;
        this.EconomicsAndTrade = EconomicsAndTrade;
        this.Law = Law;
        this.SocialScience = SocialScience;
    }

    // Getter와 Setter 메서드들

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Integer getImageResource() {
        return imageResource;
    }

    public int getProductType() {
        return productType;
    }

    public String getCompany() {
        return company;
    }

    public String getFeatures() {
        return features;
    }
    public double getRating() {
        return ratingCount > 0 ? totalRating / ratingCount : 0.0;
    }


    public double getAverageRating() {
        return ratingCount > 0 ? totalRating / ratingCount : 0.0;
    }

    public boolean isFavorite() {
        return heart;
    }

    public void setFavorite(boolean heart) {
        this.heart = heart;
    }

    public Map<String, List<Review>> getReviews() {
        return reviews;
    }

    // 사용자 수 데이터 접근 메서드
    public int getIT() {
        return IT;
    }

    public int getEnglish() {
        return English;
    }

    public int getNaturalScience() {
        return NaturalScience;
    }

    public int getEconomicsAndTrade() {
        return EconomicsAndTrade;
    }

    public int getLaw() {
        return Law;
    }

    public int getSocialScience() {
        return SocialScience;
    }
}
