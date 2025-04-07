package com.example.sportmot.data.entities;

import com.google.gson.annotations.SerializedName;

/*We need this wrapper because of the way Challonge returns teams */
//Þurfum að samhæfa þetta. Blanda fyrir gömul mót og mín mót
public class ChallongeTeamWrapper {
    private ChallongeTeam participant;
    private ChallongeTeam teams;

    @SerializedName("participant")
    private ChallongeTeam team;

    public ChallongeTeam getTeam() {
        return team;
    }

    public void setParticipant(ChallongeTeam participant) {
        this.teams = participant;
    }

    public ChallongeTeam getParticipant() {
        return participant;
    }

    public void setParticipant(ChallongeTeam participant) {
        this.participant = participant;
    }
}



