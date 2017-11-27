package com.example.sombra.myapplication;

/**
 * Created by Sombra on 2017-11-27.
 */

public class Session {

    private static String username;

    public static void setUsername(String user) {
        Session.username = user;
    }

    public static String getUsername() {
        return username;
    }

}
