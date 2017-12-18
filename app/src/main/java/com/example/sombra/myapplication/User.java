package com.example.sombra.myapplication;

import android.content.Context;

/**
 * Created by Sombra on 2017-11-27.
 */

public class User {
    private static String username = null;
    private static Context context;

    public static void setUsername(String user) {
        User.username = user;
    }

    public static void setAppContext(Context context) {
        User.context = context;
    }

    public static Context getAppContext() {
        return context;
    }

    public static String getUsername() {
        return username;
    }
}
