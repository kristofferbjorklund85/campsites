package com.example.sombra.myapplication;

/**
 * Created by Sombra on 2017-12-20.
 */

public class Rating {

    private String userId;
    private int rating;

    public Rating(String userId, int rating) {
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
