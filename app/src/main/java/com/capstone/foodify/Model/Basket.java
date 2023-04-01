package com.capstone.foodify.Model;

public class Basket {
    private String id;
    private String img;
    private String name;
    private float cost;
    private String shopName;
    private String quantity;
    private float discountPercent;
    private int shopId;

    public Basket(String id, String img, String name, float cost, String shopName, String quantity, float discountPercent, int shopId) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.cost = cost;
        this.shopName = shopName;
        this.quantity = quantity;
        this.discountPercent = discountPercent;
        this.shopId = shopId;
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

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public float getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(float discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
}
