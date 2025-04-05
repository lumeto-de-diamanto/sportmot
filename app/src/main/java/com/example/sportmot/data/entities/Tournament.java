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

    // Default constructor (existing one)
    public Tournament() {
        // Initialize default values if necessary
    }

    // Constructor with the parameters for TournamentNew
    public Tournament(int id, String tournamentName, String startTime, String endTime, String tournamentUrl) {
        this.id = id;
        this.tournamentName = tournamentName;

        // You might want to convert the string times to List<Integer> here if needed
        this.startTime = convertTimeStringToList(startTime);
        this.endTime = convertTimeStringToList(endTime);

        // If tournamentUrl is not necessary for your logic, you can ignore it
        // Otherwise, store it if needed
    }

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

    // Helper method to convert time string to List<Integer>
    private List<Integer> convertTimeStringToList(String timeString) {
        // For example, you might convert "2025-03-30T14:30:00" to a list of [14, 30]
        // Implement the logic here to extract relevant time values as integers
        return null;  // Replace with actual conversion logic

    }
}

