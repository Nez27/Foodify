package com.capstone.foodify.Model.Response;

import com.capstone.foodify.Model.Page;
import com.capstone.foodify.Model.Shop.Shop;

import java.util.List;

public class Shops {
    private List<Shop> shops;
    private Page page;

    public List<Shop> getShops() {
        return shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
