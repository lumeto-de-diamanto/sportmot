package com.example.sportmot.data.entities;

import com.google.gson.annotations.SerializedName;

//We need this wrapper because of the way Challonge returns teams
public class ChallongeTeamWrapper {

    @SerializedName("participant")
    private ChallongeTeam team;

    public ChallongeTeam getTeam() {
        return team;
    }

    public void setParticipant(ChallongeTeam participant) {
        this.team = participant;
    }

}