package com.example.easyspec;

public class ProductItem {
        private String name; // 이름
        private int price; // 가격
        private int imageResource; // 이미지 파일
        private double rating; // 평점
        private boolean heart; // 즐겨찾기
        private int productType; // 1 -> 노트북 2 -> 태블릿 3 -> 핸드폰

        private String key; //firebase의 productID저장

        // 생성자 - 신경 안써도 괜찮음
        public ProductItem() {
            this.name = "Iphone 15 pro";
            this.price = 1500000;
            this.imageResource = R.drawable.iphone15_promax;
            this.rating = 4.5;
            this.heart = true;
            this.productType = 3;
        }
        /*
        public ProductItem(String name, int price, int imageResource, double rating, boolean heart) {
            this.name = name;
            this.price = price;
            this.imageResource = imageResource;
            this.rating = rating;
            this.heart = heart;
        }
         */

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

        public String getKey(){
            return key;
        }
        public void setKey(String key){
            this.key = key;
        }

        public int getProductType(){
            return productType;
        }

        public void setProductType(int productType){
            this.productType = productType;
        }
}
