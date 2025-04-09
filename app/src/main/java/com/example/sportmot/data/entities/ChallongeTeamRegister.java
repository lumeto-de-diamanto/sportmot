package com.example.sportmot.data.entities;

//Request model used for sending new teams to Challonge via API
public class ChallongeTeamRegister {

    public String name;
    public String misc;

    public ChallongeTeamRegister(String name, String misc) {
        this.name = name;
        this.misc = misc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }
}
