package com.example.sportmot.api;

import com.example.sportmot.data.entities.ChallongeTeamRegisterWrapper;
import com.example.sportmot.data.entities.ChallongeTeamResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

//Register team to challonge schedule
public interface TeamRegisterApiService {
    @POST("tournaments/{cid}/participants.json")
    Call<ChallongeTeamResponse> registerTeamToChallonge(
            @Path("cid") int tournamentId,
            @Query("api_key") String apiKey,
            @Body ChallongeTeamRegisterWrapper participant
    );

}
