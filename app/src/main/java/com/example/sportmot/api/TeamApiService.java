package com.example.sportmot.api;

import com.example.sportmot.data.entities.Club;
import com.example.sportmot.data.entities.Team;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

//Connect to api
public interface TeamApiService {
    @POST("/addteam")
    Call<String> createTeam(@Body Team team);

    @GET("rest/loadClubs")
    Call<List<Club>> loadClubs();
}
