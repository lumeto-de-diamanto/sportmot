package com.example.sportmot.data.entities;

import com.google.gson.annotations.SerializedName;
public class TournamentNew {
    private int id;

    @SerializedName("name")
    private String tournamentName;

    @SerializedName("start_at")
    private String startTime;

    @SerializedName("completed_at")
    private String endTime;

    @SerializedName("full_challonge_url")
    private String tournamentUrl;
    @SerializedName("date")
    private String tournamentDate;
    public int getId() { return id; }
    public String getTournamentName() { return tournamentName; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getTournamentUrl() { return tournamentUrl; }
    public String getTournamentDate() {return tournamentDate; }
}

