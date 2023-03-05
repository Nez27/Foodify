package com.capstone.foodify.Model.Basket;

public class Basket {
    private String id;
    private String img;
    private String name;
    private String price;
    private String shopName;
    private String quantity;
    private String discount;

    public Basket(String id, String img, String name, String price, String shopName, String quantity, String discount) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.price = price;
        this.shopName = shopName;
        this.quantity = quantity;
        this.discount = discount;
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
    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
