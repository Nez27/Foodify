package com.capstone.foodify.Model;

import java.util.List;

public class Food implements Comparable<Food>{
    private String id;
    private String name;
    private String description;
    private boolean isEnabled;
    private float discountPercent;
    private float cost;
    private float averageRating;
    private String reviewCount;
    private Shop shop;
    private List<Category> categories;
    private List<Image> images;
    private List<Comment> comments;
    public Food() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public float getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(float discountPercent) {
        this.discountPercent = discountPercent;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int compareTo(Food food) {
        if(getName().isEmpty() || food.getName().isEmpty()){
            return 0;
        }
        return getName().compareTo(food.getName());
    }

    @Override
    public String toString() {
        return "Food{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isEnabled=" + isEnabled +
                ", discountPercent=" + discountPercent +
                ", cost=" + cost +
                ", averageRating=" + averageRating +
                ", commentCount='" + reviewCount + '\'' +
                ", shop=" + shop +
                ", categories=" + categories +
                ", images=" + images +
                '}';
    }
}
