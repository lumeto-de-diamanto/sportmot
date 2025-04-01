package com.example.sportmot.data.entities;

import java.util.List;

public class Tournament {
    private int id;
    private String tournamentName;
    private List<Integer> startTime;
    private List<Integer> endTime;
    private List<Integer> tournamentDate;
    private int fields;
    private List<Integer> deadlineForReg;
    private int numberOfGroups;
    private int teamsPerGroup;
    private int gameLength;
    private int tournamentID;
    private double latitude;
    private double longitude;

    // Getters
    public int getId() { return id; }
    public String getTournamentName() { return tournamentName; }
    public List<Integer> getStartTime() { return startTime; }
    public List<Integer> getEndTime() { return endTime; }
    public List<Integer> getTournamentDate() { return tournamentDate; }
    public int getFields() { return fields; }
    public int getNumberOfGroups() { return numberOfGroups; }
    public int getTeamsPerGroup() { return teamsPerGroup; }
    public int getGameLength() { return gameLength; }

    public int getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
