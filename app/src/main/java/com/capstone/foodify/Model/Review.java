package com.capstone.foodify.Model;

public class Review {
    private String name;
    private int ratingScore;
    private String content;

    public Review() {
    }

    public Review(String name, int ratingScore, String content) {
        this.name = name;
        this.ratingScore = ratingScore;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(int ratingScore) {
        this.ratingScore = ratingScore;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
