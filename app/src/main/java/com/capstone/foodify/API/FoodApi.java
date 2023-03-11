package com.capstone.foodify.API;

import com.capstone.foodify.Model.Category.Category;
import com.capstone.foodify.Model.DistrictWard.DistrictWardResponse;
import com.capstone.foodify.Model.Food.Food;
import com.capstone.foodify.Model.Response.Foods;
import com.capstone.foodify.Model.Response.Shops;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface FoodApi {

    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").create();

    FoodApi apiService = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FoodApi.class);

    @GET("products?pageNo=0&pageSize=10&sortBy=id&sortDir=asc")
    Call<Foods> recommendFood();

    @GET("products?pageNo=0&pageSize=10&sortBy=createdTime&sortDir=desc")
    Call<Foods> recentFood();

    @GET("shops?pageNo=0&pageSize=10&sortBy=id&sortDir=asc")
    Call<Shops> allShops();

    @GET("products/search/{name}")
    Call<Foods> searchFoodByName(@Path("name") String name, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @GET("products")
    Call<Foods> listFood(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @GET("categories")
    Call<List<Category>> listCategory();

    @GET("products/categories")
    Call<Foods> listFoodByCategory(@Query("id") List<Integer> id, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @GET("products/{id}")
    Call<Food> detailFood(@Path("id") String id);

//    @GET("detail/{id}/image")
//    Call<List<ImageFood>> getImageFoodById(@Path("id") String id);

    @GET("district/{districtId}/ward")
    Call<List<DistrictWardResponse>> wardResponse(@Path("districtId") int district_id);

    @GET("district")
    Call<List<DistrictWardResponse>> districtResponse();

    @GET("wishlist")
    Call<List<Food>> getListFavoriteFood();

    @POST("wishlist")
    Call<Food> addFoodToFavorite(@Body Food food);

    @DELETE("wishlist/{id}")
    Call<Food> removeFoodFromFavorite(@Path("id") int id);
}
