package com.example.sportmot.api;

import com.example.sportmot.data.entities.Result;
import com.example.sportmot.data.entities.ChallongeTeam;
import com.example.sportmot.data.entities.ChallongeTeamWrapper;
import com.example.sportmot.data.entities.MatchWrapper;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ResultsApiService {
    //@GET("tournaments/{tournamentId}/matches.json?api_key=tuIVdCZqQmHUhhc4QdjgOpwYLI2T2AAX7eq7lycr")
    //Call<List<Result>> getResults(@Path("tournamentId") int tournamentId);
    //@GET("tournaments/{tournamentId}/matches.json")
    //Call<List<Result>> getResults(@Path("tournamentId") String tournamentId, @Query("api_key") String apiKey);
    @GET("tournaments/{tournament_id}/matches.json")
    Call<List<MatchWrapper>> getResults(@Path("tournament_id") String tournamentId, @Query("api_key") String apiKey);

    @GET("tournaments/{tournament_id}/participants.json")
    Call<List<ChallongeTeamWrapper>> getParticipants(@Path("tournament_id") String tournamentId, @Query("api_key") String apiKey);

}
