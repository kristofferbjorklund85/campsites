package com.example.sombra.camps;

/**
 * Model for a comment. Only contains the constructor for the class.
 */

public class CommentModel {

    String id;
    String campsiteId;
    String date;
    String userId;
    String username;
    String commentBody;

    public CommentModel(String id, String campsiteId, String date, String userId, String username, String commentBody){
        this.id = id;
        this.campsiteId = campsiteId;
        this.date = date;
        this.userId = userId;
        this.username = username;
        this.commentBody = commentBody;
    }

}
