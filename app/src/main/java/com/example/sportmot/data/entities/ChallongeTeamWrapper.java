package com.example.sportmot.data.entities;

/*We need this wrapper because of the way Challonge returns teams */

import com.google.gson.annotations.SerializedName;

public class ChallongeTeamWrapper {
    private ChallongeTeam teams;

    @SerializedName("participant")
    private ChallongeTeam team;

    public ChallongeTeam getTeam() {
        return team;
    }

    public void setParticipant(ChallongeTeam participant) {
        this.teams = participant;
    }
}