package com.capstone.foodify.API;

import static com.capstone.foodify.Common.TOKEN;

import androidx.annotation.NonNull;

import com.capstone.foodify.Model.Food.Food;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FoodApiToken {
    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").create();

    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request newRequest = chain.request().newBuilder().addHeader("Authorization", "Bearer " + TOKEN).build();
            return chain.proceed(newRequest);
        }
    }).build();


    FoodApiToken apiService = new Retrofit.Builder()
            .client(client)
            .baseUrl("http://10.0.2.2:8080/api/").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FoodApiToken.class);

    @GET("wishlist")
    Call<List<Food>> getListFavoriteFood();

    @POST("users/{userId}/loves/{productId}")
    Call<Food> addFoodToFavorite(@Path("userId") String userId, @Path("productId") String productId);

    @DELETE("wishlist/{id}")
    Call<Food> removeFoodFromFavorite(@Path("id") int id);
}
