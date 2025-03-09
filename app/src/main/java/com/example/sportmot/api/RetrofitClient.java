package com.example.sportmot.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://sportmot.onrender.com/";

    public static Retrofit getClient() {
        if (retrofit == null) {

            // Create the logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Set logging level to BODY for full response logging

            // Build the OkHttpClient with the logging interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)  // Add the interceptor
                    .build();

            // Initialize Retrofit with the OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)  // Use the client with logging interceptor
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static TeamApiService getApiService() {
        return getClient().create(TeamApiService.class);
    }

    public static ClubApiService getClubApiService() {
        return getClient().create(ClubApiService.class);
    }
}


