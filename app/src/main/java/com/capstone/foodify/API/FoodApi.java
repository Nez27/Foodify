package com.capstone.foodify.API;

import com.capstone.foodify.Model.Food.Food;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public interface FoodApi {

    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").create();

    FoodApi apiService = new Retrofit.Builder()
            .baseUrl("https://free-food-menus-api-production.up.railway.app/").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FoodApi.class);

    @GET("best-foods")
    Call<List<Food>> bestFoodResponse();

    @GET("drinks")
    Call<List<Food>> drinksFoodResponse();
}
