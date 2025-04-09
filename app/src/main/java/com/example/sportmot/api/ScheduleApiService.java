package com.example.sportmot.api;

//import com.example.sportmot.data.entities.Tournament;
//import com.example.sportmot.data.entities.Game;
//import com.example.sportmot.data.entities.Team;

import com.example.sportmot.data.entities.ChallongeTeamWrapper;
import com.example.sportmot.data.entities.TournamentNewWrapper;
import com.example.sportmot.data.entities.TournamentResults;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ScheduleApiService {

    @GET("tournaments.json")
    Call<List<TournamentNewWrapper>> getTournaments(@Query("api_key") String apiKey);
    @GET("tournaments/{id}/results")
    Call<TournamentResults> getTournamentResults(@Path("id") String tournamentId, @Query("api_key") String apiKey);
    @GET("tournaments/{tournament_id}/participants.json")
    Call<List<ChallongeTeamWrapper>> getTeams(
            @Path("tournament_id") String tournamentId,
            @Query("api_key") String apiKey
    );

}
