package com.example.sportmot.data.entities;


import android.os.Parcel;
import android.os.Parcelable;

public class Team implements Parcelable {
    private String teamName;
    private Club club;
    private String level;
    private String teamId;

    // Constructor
    public Team(String teamId,String teamName, Club club, String level) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.club = club;
        this.level = level;
    }

    // Parcelable constructor
    protected Team(Parcel in) {
        teamName = in.readString();
        club = in.readParcelable(Club.class.getClassLoader());
        level = in.readString();
    }

    // Parcelable Creator
    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(teamName);
        dest.writeParcelable((Parcelable) club, flags);
        dest.writeString(level);
    }


    public String getTeamName() {
        return teamName;
    }


    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public String getTeamId() {
        return teamId;

    }

}
