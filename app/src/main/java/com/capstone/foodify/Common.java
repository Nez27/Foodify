package com.capstone.foodify;

import com.capstone.foodify.Model.Basket.Basket;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Common {
    public static List<Basket> LIST_BASKET_FOOD = new ArrayList<>();

    public static String changeCurrencyUnit(float price){
        Locale locale = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        return fmt.format(price);
    }
}
