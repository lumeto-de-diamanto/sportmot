package com.example.sportmot.api;
import com.example.sportmot.data.entities.Tournament;
import com.example.sportmot.data.entities.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface TournamentApiService {

    // user signup (creating a new user)
    @POST("/rest/signup")
    Call<String> signUp(@Body User user);

    @POST("/rest/login")
    @FormUrlEncoded
    Call<String> login(
            @Field("userID") String userID,
            @Field("password") String password);

    @GET("/rest/tournaments")
    Call<List<Tournament>> getCurrentTournaments(@Query("date") String date);

    @GET("/rest/tournaments")
    Call<List<Tournament>> getUpcomingTournaments(@Query("date") String date);

    @GET("/rest/tournaments")
    Call<List<Tournament>> getOldTournaments(@Query("date") String date);
}
