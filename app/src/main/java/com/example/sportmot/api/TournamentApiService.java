package com.example.sportmot.api;
import com.example.sportmot.data.entities.Tournament;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface TournamentApiService {
    @GET("/rest/tournaments")
    //Call<List<Tournament>> getTournaments();
    Call<List<Tournament>> getCurrentTournaments(@Query("date") String date);
}
