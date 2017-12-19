package com.example.sombra.myapplication;

import java.util.UUID;

/**
 * Created by Samuel on 2017-12-19.
 */

public class UserModel {

    String id;
    String username;
    String password;

    public UserModel(String username, String password) {
        this.id = UUID.randomUUID().toString();
        this.id = username;
        this.password = password;
    }
}
