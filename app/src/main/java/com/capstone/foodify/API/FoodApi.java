package com.capstone.foodify.API;

import com.capstone.foodify.Model.Category.Category;
import com.capstone.foodify.Model.DistrictWard.DistrictWardResponse;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Food.ImageFood;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface FoodApi {

    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").create();

    FoodApi apiService = new Retrofit.Builder()
            .baseUrl("https://63eb52abbfdd4299674540d4.mockapi.io/api/v1/").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FoodApi.class);

    @GET("randomProduct")
    Call<List<Food>> bestFoodResponse();

    @GET("recommend")
    Call<List<Food>> drinksFoodResponse();

    @GET("listFood")
    Call<List<Food>> listFood(@Query("search") String search, @Query("page") int page, @Query("limit") int limit);

    @GET("listFood")
    Call<List<Food>> listFood(@Query("page") int page, @Query("limit") int limit);

    @GET("Category")
    Call<List<Category>> listCategory();

    @GET("Category/{id}/food")
    Call<List<Food>> listFoodByCategory(@Path("id") String id);

    @GET("detail/{id}")
    Call<Food> detailFood(@Path("id") String id);

    @GET("detail/{id}/image")
    Call<List<ImageFood>> getImageFoodById(@Path("id") String id);

    @GET("district/{districtId}/ward")
    Call<List<DistrictWardResponse>> wardResponse(@Path("districtId") int district_id);

    @GET("district")
    Call<List<DistrictWardResponse>> districtResponse();
}
