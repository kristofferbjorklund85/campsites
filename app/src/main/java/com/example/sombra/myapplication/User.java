package com.example.sombra.myapplication;

/**
 * Created by Sombra on 2017-11-27.
 */

public class User {

    private static String username = null;

    public static void setUsername(String user) {
        User.username = user;
    }

    public static String getUsername() {
        return username;
    }

}
