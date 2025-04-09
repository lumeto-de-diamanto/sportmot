package com.example.sportmot.data.entities;

import com.google.gson.annotations.SerializedName;
public class TournamentWrapper {
    @SerializedName("tournament")
    private Tournament tournament;
    public Tournament getTournament() {
        return tournament;
    }
}

