package com.example.easyspec;

import java.util.ArrayList;
import java.util.List;

public class ProductItem{
    private String name; // 이름, 불변함
    private int price; // 가격, 불변함
    private int imageResource; // 이미지 파일, 불변함
    private double rating; // 평점
    private boolean heart; // 즐겨찾기
    private int productType; // 1 -> 노트북 2 -> 태블릿 3 -> 핸드폰
    //private UsersOfTheProduct usersOfTheProduct;

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
        public boolean heart() {
            return heart;
        }
}
