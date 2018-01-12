package com.example.sombra.myapplication;

/**
 * 
 */
public class RatingModel {

    private String userId;
    private int rating;

    public RatingModel(String userId, int rating) {
        this.userId = userId;
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public int getRating() {
        return rating;
    }

}
