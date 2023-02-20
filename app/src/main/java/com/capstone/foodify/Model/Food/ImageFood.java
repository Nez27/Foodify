package com.capstone.foodify.Model.Food;

public class ImageFood {
    private String imageUrl;
    private String id;
    private String detailId;

    public ImageFood(String imageUrl, String id, String detailId) {
        this.imageUrl = imageUrl;
        this.id = id;
        this.detailId = detailId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }
}
