package com.example.sportmot.data.entities;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("id")
    private int matchId;
    @SerializedName("tournament_id")
    private int tournamentId;
    @SerializedName("player1_id")
    private int player1Id;
    @SerializedName("player2_id")
    private int player2Id;
    @SerializedName("scores_csv")
    private String scoresCsv;

    // Getters and setters
    public int getPlayer1Id() {
        return player1Id;
    }
    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }
    public int getPlayer2Id() {
        return player2Id;
    }
    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }
    public String getScoresCsv() {
        return scoresCsv;
    }
    public void setScoresCsv(String scoresCsv) {
        this.scoresCsv = scoresCsv;
    }


    public int getMatchId() {
        return matchId;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    // Helper methods to extract scores if needed
    public int getScore1() {
        if (scoresCsv != null && !scoresCsv.isEmpty()) {
            String[] scores = scoresCsv.split("-");
            if (scores.length == 2) {
                try {
                    return Integer.parseInt(scores[0]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    public int getScore2() {
        if (scoresCsv != null && !scoresCsv.isEmpty()) {
            String[] scores = scoresCsv.split("-");
            if (scores.length == 2) {
                try {
                    return Integer.parseInt(scores[1]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
}
