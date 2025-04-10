package com.example.android_food_app.Model;

public class Feedback {
    private String ID;
    private String userID;
    private String comment;
    private String date; // New field for date

    // Constructor with all fields
    public Feedback(String ID, String userID, String comment, String date) {
        this.ID = ID;
        this.userID = userID;
        this.comment = comment;
        this.date = date;
    }

    // Default constructor
    public Feedback() {
    }

    // Getter and Setter methods
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
