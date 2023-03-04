package com.capstone.foodify.Model.Food;

public class Food implements Comparable<Food>{
    private String id;
    private String img;
    private String name;
    private String price;
    private String discount;
    private String description;
    private String shopName;
    private String reviewCount;
    public Food() {
    }
    public Food(String id, String img, String name, String price, String shopName) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.price = price;
        this.shopName = shopName;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getDiscount() {
        return discount;
    }
    public void setDiscount(String discount) {
        this.discount = discount;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getReviewCount() {
        return reviewCount;
    }
    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    @Override
    public int compareTo(Food food) {
        if(getName().isEmpty() || food.getName().isEmpty()){
            return 0;
        }
        return getName().compareTo(food.getName());
    }
}
