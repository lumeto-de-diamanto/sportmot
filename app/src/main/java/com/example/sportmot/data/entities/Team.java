package com.example.sportmot.data.entities;

public class Team {
    private String teamId;
    private String teamName;
    public Team(String teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamId() {
        return teamId;
    }

}
