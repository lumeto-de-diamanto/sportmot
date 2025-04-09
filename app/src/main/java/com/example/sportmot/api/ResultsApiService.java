package com.example.sportmot.api;

import com.example.sportmot.data.entities.Result;
import com.example.sportmot.data.entities.ChallongeTeam;
import com.example.sportmot.data.entities.ChallongeTeamWrapper;
import com.example.sportmot.data.entities.MatchWrapper;
import com.example.sportmot.data.entities.User;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ResultsApiService {

    @GET("tournaments/{tournament_id}/matches.json")

    Call<List<MatchWrapper>> getResults(@Path("tournament_id") String tournamentId,
                                        @Query("api_key") String apiKey);

    @GET("tournaments/{tournament_id}/participants.json")
    Call<List<ChallongeTeamWrapper>> getParticipants(@Path("tournament_id") String tournamentId,
                                                     @Query("api_key") String apiKey);

    //https://api.challonge.com/v1/tournaments/{tournament}/matches/{match_id}.{json|xml}
    @PUT("tournaments/{tournament_id}/matches/{match_id}.json")
    Call<MatchWrapper> updateScore(@Path("tournament_id") String tournamentId,
                                   @Path("match_id") String matchId,
                                   @Query("match[scores_csv]") String score,
                                   @Query("api_key") String apiKey);
}
