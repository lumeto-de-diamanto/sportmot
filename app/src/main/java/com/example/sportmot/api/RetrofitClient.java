package com.example.sportmot.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://sportmot.onrender.com/";

    public static Retrofit getClient() {
        if (retrofit == null) {

            // Logging for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    // Allow handling of plain String responses:
                    .addConverterFactory(ScalarsConverterFactory.create())
                    // Keep Gson for JSON objects:
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
