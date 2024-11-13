package com.example.easyspec;

public class ProductLayoutItem {
        private String name;
        private int price;
        private int imageResource;
        private float rating;
        private boolean heart;


        public ProductLayoutItem(String name, int price, int imageResource, float rating, boolean heart) {
            this.name = name;
            this.price = price;
            this.imageResource = imageResource;
            this.rating = rating;
            this.heart = heart;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public int getImageResource() {
            return imageResource;
        }

        public float getRating() {
            return rating;
        }
        public boolean heart() {
            return heart;
        }
}
