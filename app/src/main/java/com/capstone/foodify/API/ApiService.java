package com.capstone.foodify.API;

import com.capstone.foodify.DistrictWardResponse.DisctrictWardResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiService {

    Gson gson = new GsonBuilder().setDateFormat("HH:mm:ss dd-MM-yyyy").create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://vn-public-apis.fpo.vn/").addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("wards/getByDistrict")
    Call<DisctrictWardResponse> wardResponse(@Query("districtCode") int districtCode, @Query("limit") int limit);

    @GET("districts/getByProvince")
    Call<DisctrictWardResponse> districtResponse(@Query("provinceCode") int provinceCode, @Query("limit") int limit);
}
