package com.capstone.foodify.Model;

import java.util.List;

public class Menu {

    private String nameMenu;
    private List<Food> foods;

    public Menu(String nameMenu, List<Food> foods) {
        this.nameMenu = nameMenu;
        this.foods = foods;
    }

    public String getNameMenu() {
        return nameMenu;
    }

    public void setNameMenu(String nameMenu) {
        this.nameMenu = nameMenu;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }
}
