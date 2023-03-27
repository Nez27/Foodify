package com.capstone.foodify.Model;

public class Comment {
    private int id;
    private String content;
    private float rating;
    private int userId;
    private User user;

    public Comment(String content, float rating, int userId) {
        this.content = content;
        this.rating = rating;
        this.userId = userId;
}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
