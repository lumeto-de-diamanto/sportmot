package com.example.sportmot.data.entities;
import com.google.gson.annotations.SerializedName;

public class MatchWrapper {
    @SerializedName("match")
    private Result match;

    public Result getMatch() {
        return match;
    }

    public void setMatch(Result match) {
        this.match = match;
    }
}
