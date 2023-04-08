package com.capstone.foodify.API;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapDirectionApi {

    GoogleMapDirectionApi apiService = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/").addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(GoogleMapDirectionApi.class);

    @GET("maps/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination,
                               @Query("key") String key);
}
