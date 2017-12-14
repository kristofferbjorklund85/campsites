package com.example.sombra.myapplication;

/**
 * Created by Sombra on 2017-11-28.
 */

public class Comment {

    String id;
    String campsiteId;
    String date;
    String username;
    String commentBody;

    public Comment(String id, String campsiteId, String date, String username, String commentBody){
        this.id = id;
        this.campsiteId = campsiteId;
        this.date = date;
        this.username = username;
        this.commentBody = commentBody;
    }

}
