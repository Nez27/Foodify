package com.capstone.foodify.DistrictWardResponse;

import java.util.List;

public class Data {
    private int nItems;
    private int nPages;
    private List<Datas> data;

    public int getnItems() {
        return nItems;
    }

    public void setnItems(int nItems) {
        this.nItems = nItems;
    }

    public int getnPages() {
        return nPages;
    }

    public void setnPages(int nPages) {
        this.nPages = nPages;
    }

    public List<Datas> getData() {
        return data;
    }

    public void setData(List<Datas> data) {
        this.data = data;
    }
}
