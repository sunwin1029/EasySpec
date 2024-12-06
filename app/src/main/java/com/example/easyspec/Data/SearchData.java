package com.example.easyspec.Data;

public class SearchData {

    private int productType;
    private String name;
    private int minimumPrice;
    private int maxPrice;


    public SearchData(int productType, String name, int minimumPrice, int maxPrice) {
        this.productType = productType;
        this.name = name;
        this.minimumPrice = minimumPrice;
        this.maxPrice = maxPrice;
    }

    public int getProductType() {
        return productType;
    }

    public String getName() {
        return name;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public int getMinimumPrice() {
        return minimumPrice;
    }
}
