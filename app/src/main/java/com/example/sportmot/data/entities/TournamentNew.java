package com.example.sportmot.data.entities;

import com.google.gson.annotations.SerializedName;
public class TournamentNew {
    private int id;

    @SerializedName("name") // API key for tournament name
    private String tournamentName;

    @SerializedName("start_at") // API key for tournament start time
    private String startTime;

    @SerializedName("completed_at") // API key for tournament end time
    private String endTime;

    @SerializedName("full_challonge_url") // API key for tournament link
    private String tournamentUrl;
    @SerializedName("date") // API key for tournament date
    private String tournamentDate;

    public int getId() { return id; }
    public String getTournamentName() { return tournamentName; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getTournamentUrl() { return tournamentUrl; }

    public String getTournamentDate() {return tournamentDate; }
}

