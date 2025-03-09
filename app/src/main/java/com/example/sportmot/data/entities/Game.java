package com.example.sportmot.data.entities;

public class Game {
    private String teamId;
    private String opponent;
    private String date;
    private String time;

    public Game(String teamId, String opponent, String date, String time) {
        this.teamId = teamId;
        this.opponent = opponent;
        this.date = date;
        this.time = time;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getOpponent() {
        return opponent;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
