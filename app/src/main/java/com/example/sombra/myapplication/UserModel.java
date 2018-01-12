package com.example.sombra.myapplication;

/**
 * UserModel is the model of our user.
 * Only contains the constructor for the class.
 */
public class UserModel {

    String id;
    String username;
    String password;

    public UserModel(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
