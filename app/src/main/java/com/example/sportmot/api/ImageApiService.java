package com.example.sportmot.api;

import android.graphics.Bitmap;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageApiService {

    @GET("/images/{teamID}")
    Call<ResponseBody> getImage(
            @Path("teamID") String teamID);

    @Multipart
    @POST("/images/")
    Call<String> postImage(
            @Part("team") RequestBody teamID,
            @Part MultipartBody.Part image);
}
