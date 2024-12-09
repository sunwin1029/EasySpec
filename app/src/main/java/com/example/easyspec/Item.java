package com.example.easyspec;

public class Item {
    private final String title;
    private final String subtitle;
    private final String description;
    private final int imageResId;

    public Item(String title, String subtitle, String description, int imageResId) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }
}
