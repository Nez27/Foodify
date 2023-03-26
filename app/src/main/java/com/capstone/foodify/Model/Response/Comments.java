package com.capstone.foodify.Model.Response;

import com.capstone.foodify.Model.Comment;
import com.capstone.foodify.Model.Page;

import java.util.List;

public class Comments {
    private List<Comment> comments;
    private Page page;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
