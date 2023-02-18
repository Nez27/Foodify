package com.capstone.foodify.Model.Food;

public class Food {
    private String id;
    private String img;
    private String name;
    private String price;

    public Food() {
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


    @Override
    public String toString() {
        return "Food{" +
                "id='" + id + '\'' +
                ", img='" + img + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
