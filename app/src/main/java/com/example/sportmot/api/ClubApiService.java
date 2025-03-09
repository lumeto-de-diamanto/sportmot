package com.example.sportmot.api;

import com.example.sportmot.data.entities.Club;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ClubApiService {
    @GET("rest/loadClubs")
    Call<List<Club>> loadClubs();
}
