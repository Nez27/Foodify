package com.capstone.foodify.API;

import com.capstone.foodify.Common;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

public interface TokenFCMFirebaseAPI {

    TokenFCMFirebaseAPI apiService = new Retrofit.Builder()
            .baseUrl(Common.BASE_URL).addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(TokenFCMFirebaseAPI.class);

    @GET("users/{userId}/fcm")
    Call<String> getTokenFCM();
}
