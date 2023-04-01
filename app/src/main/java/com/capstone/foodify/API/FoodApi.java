package com.capstone.foodify.API;

import com.capstone.foodify.Model.Category;
import com.capstone.foodify.Model.Comment;
import com.capstone.foodify.Model.DistrictWardResponse;
import com.capstone.foodify.Model.Food;
import com.capstone.foodify.Model.Response.Categories;
import com.capstone.foodify.Model.Response.Comments;
import com.capstone.foodify.Model.Response.CustomResponse;
import com.capstone.foodify.Model.Response.Foods;
import com.capstone.foodify.Model.Response.Shops;
import com.capstone.foodify.Model.Shop;
import com.capstone.foodify.Model.Slider;
import com.capstone.foodify.Model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface FoodApi {
    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").setLenient().create();

    FoodApi apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.183:8080/api/").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FoodApi.class);

    @GET("products/random?pageSize=10")
    Call<Foods> recommendFood();

    @GET("products/enable?pageNo=0&pageSize=10&sortBy=createdTime&sortDir=desc")
    Call<Foods> recentFood();

    @GET("shops/enable?pageNo=0&pageSize=10&sortBy=id&sortDir=asc")
    Call<Shops> allShops();

    @GET("products/search/{name}")
    Call<Foods> searchFoodByName(@Path("name") String name, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @GET("products")
    Call<Foods> listFood(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @GET("categories/randoms?pageNo=0&pageSize=7")
    Call<Categories> listCategory();

    @GET("products/categories")
    Call<Foods> listFoodByCategory(@Query("id") List<Integer> id, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @GET("products/categoriesAndName")
    Call<Foods> listFoodByCategoriesAndName(@Query("id") List<Integer> id, @Query("name") String name, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @GET("products/{id}")
    Call<Food> detailFood(@Path("id") String id);

    @GET("shops/{id}")
    Call<Shop> detailShop(@Path("id") int id);

    @GET("products/shops/{id}")
    Call<Foods> listFoodByShopId(@Path("id") int id, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @GET("districts/{districtId}/wards")
    Call<List<DistrictWardResponse>> wardResponse(@Path("districtId") int district_id);

    @GET("districts")
    Call<List<DistrictWardResponse>> districtResponse();

    @GET("sliders")
    Call<List<Slider>> listSlider();

    @Headers({"accept: */*", "Content-Type: application/json"})
    @POST("auth/signup")
    Call<User> register(@Body User user);

    @POST("auth/check")
    Call<CustomResponse> checkEmailOrPhoneExist (@Body User user);

    @GET("products/{productId}/comments")
    Call<Comments> getCommentByProductId(@Path("productId") int productId, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("sortBy") String sortBy, @Query("sortDir") String sortDir);

    @GET("products/{productId}/users/{userId}")
    Call<Comment> getUserComment(@Path("productId") int productId, @Path("userId") int userId);

    @GET("shops/{id}")
    Call<Shop> getShopById(@Path("id") int id);
}
