package com.capstone.foodify.Model.Response;

import com.capstone.foodify.Model.Category;
import com.capstone.foodify.Model.Page;

import java.util.List;

public class Categories {
    private List<Category> categories;
    private Page page;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
