package com.example.sportmot.data.entities;

import com.google.gson.annotations.SerializedName;
public class TournamentNewWrapper {
    @SerializedName("tournament")
    private TournamentNew tournament;
    public TournamentNew getTournament() {
        return tournament;
    }
}
