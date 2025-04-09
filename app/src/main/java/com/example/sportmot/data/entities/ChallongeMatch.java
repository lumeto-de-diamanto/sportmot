package com.example.sportmot.data.entities;

import com.google.gson.annotations.SerializedName;

public class ChallongeMatch {
    @SerializedName("id")
    public int id;

    @SerializedName("player1_id")
    public int player1Id;

    @SerializedName("player2_id")
    public int player2Id;

    @SerializedName("state")
    public String state;

    @SerializedName("round")
    public int round;

    // Optional fields, add if needed
    @SerializedName("scheduled_time")
    public String scheduledTime;

    @SerializedName("winner_id")
    public Integer winnerId;
}