package com.example.sportmot.data.entities;

import com.google.gson.annotations.SerializedName;

public class ChallongeTeamRegisterWrapper {
    @SerializedName("participant")
    private ChallongeTeamRegister participant;

    public ChallongeTeamRegisterWrapper(ChallongeTeamRegister teamRegister) {
        this.participant = teamRegister;
    }

    public ChallongeTeamRegister getParticipant() {
        return participant;
    }

    public void setParticipant(ChallongeTeamRegister participant) {
        this.participant = participant;

    }


}
