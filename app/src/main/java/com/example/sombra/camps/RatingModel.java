package com.example.sombra.camps;

/**
 * RatingModel is the model of our Rating.
 * Only contains the constructor for the class.
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
