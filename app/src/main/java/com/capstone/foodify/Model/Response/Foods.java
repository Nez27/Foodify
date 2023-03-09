package com.capstone.foodify.Model.Response;

import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Page;

import java.util.List;

public class Foods {
    private List<Food> products;
    private Page page;

    public List<Food> getProducts() {
        return products;
    }

    public void setProducts(List<Food> products) {
        this.products = products;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
