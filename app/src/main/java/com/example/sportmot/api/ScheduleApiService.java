package com.example.sportmot.api;

//import com.example.sportmot.data.entities.Tournament;
//import com.example.sportmot.data.entities.Game;
//import com.example.sportmot.data.entities.Team;

import com.example.sportmot.data.entities.TournamentNewWrapper;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ScheduleApiService {

    @GET("tournaments.json")
    Call<List<TournamentNewWrapper>> getTournaments(@Query("api_key") String apiKey);
}
