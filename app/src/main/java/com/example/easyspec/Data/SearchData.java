package com.example.easyspec.Data;

import java.io.Serializable;

public class SearchData implements Serializable {

    private int productType;
    private String name;
    private int minimumPrice;
    private int maxPrice;
    private int company;


    public SearchData(int productType, String name, int minimumPrice, int maxPrice, int company) {
        this.productType = productType;
        this.name = name;
        this.minimumPrice = minimumPrice;
        this.maxPrice = maxPrice;
        this.company = company;
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

    public int getCompany() {
        return company;
    }
}