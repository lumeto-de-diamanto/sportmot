package com.example.sportmot.data.entities;

import java.util.List;
import java.util.ArrayList;


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

    public Tournament(int id, String tournamentName, String startTime, String endTime, String tournamentUrl) {
        this.id = id;
        this.tournamentName = tournamentName;
        this.startTime = convertTimeStringToList(startTime);  // Convert the string to List<Integer>
        this.endTime = convertTimeStringToList(endTime);      // Convert the string to List<Integer>
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public void setStartTime(List<Integer> startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(List<Integer> endTime) {
        this.endTime = endTime;
    }

    public void setTournamentDate(List<Integer> tournamentDate) {
        this.tournamentDate = tournamentDate;
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
    }

    // Helper method to convert time string to List<Integer>
    //private List<Integer> convertTimeStringToList(String timeString) {
        // For example, you might convert "2025-03-30T14:30:00" to a list of [14, 30]
        // Implement the logic here to extract relevant time values as integers
      //  return null;  // Replace with actual conversion logic

    //}
    //public static List<Integer> convertTimeStringToList(String timeString) {
        // Example: "2025-03-30T14:30:00" -> [2025, 3, 30, 14, 30]
      //  String[] dateTimeParts = timeString.split("T");
        //String[] dateParts = dateTimeParts[0].split("-");
        //String[] timeParts = dateTimeParts[1].split(":");

        // Convert each part to Integer and return as List
        //return List.of(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]),
          //      Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
    //}
        public static List<Integer> convertTimeStringToList(String timeString) {
            if (timeString == null || timeString.isEmpty()) {
                return new ArrayList<>();  // Return an empty list if the input is null or empty
            }

            // Example: "2025-03-30T14:30:00" -> [2025, 3, 30, 14, 30]
            String[] dateTimeParts = timeString.split("T");
            if (dateTimeParts.length != 2) {
                return new ArrayList<>();  // Return an empty list if the time string format is unexpected
            }

            String[] dateParts = dateTimeParts[0].split("-");
            String[] timeParts = dateTimeParts[1].split(":");

            // Convert each part to Integer and return as List
            return List.of(
                    Integer.parseInt(dateParts[0]),
                    Integer.parseInt(dateParts[1]),
                    Integer.parseInt(dateParts[2]),
                    Integer.parseInt(timeParts[0]),
                    Integer.parseInt(timeParts[1])
            );
        }

}


