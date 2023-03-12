package com.capstone.foodify.Model.Image;

import com.capstone.foodify.Model.Page;

import java.util.List;

public class Images {
    private List<Image> images;
    private Page page;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
