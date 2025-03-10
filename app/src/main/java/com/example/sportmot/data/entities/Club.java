package com.example.sportmot.data.entities;

public class Club {
    private Integer clubID;
    private String clubName;

    public int getId() {
        return clubID;
    }

    public void setId(int id) {
        this.clubID = id;
    }


    public String getName() {
        return clubName;
    }

    public void setName(String name) {
        this.clubName = name;
    }

    @Override
    public String toString() {
        return clubName;
    }
}
