package com.example.sportmot.api;

import com.example.sportmot.data.entities.Club;
import com.example.sportmot.data.entities.Team;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

//Connect to api
public interface TeamApiService {
    @POST("/rest/addteam")
    Call<String> createTeam(@Body Team team);

    @GET("/rest/teams")
    Call<List<TeamWithoutClub>> getTeams();

    static class TeamWithoutClub {
        private String teamName;
        private String level;
        @SerializedName("teamID")
        @Expose
        private int teamId;
        public String getTeamName() {
            return teamName;
        }
        public String getLevel() {
            return level;
        }
        public int getTeamId() {
            return teamId;
        }
    }
}
