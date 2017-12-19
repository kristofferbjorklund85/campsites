package com.example.sombra.myapplication;

import android.content.Context;

/**
 * Created by Sombra on 2017-11-27.
 */

public class UserSingleton {
    private static String username = "guest";
    private static String id = null;
    private static Context context;

    public static void setUsername(String user) {UserSingleton.username = user;}

    public static void setId(String id) {UserSingleton.id = id;}

    public static void setAppContext(Context context) {UserSingleton.context = context;}

    public static String getUsername() {return username;}

    public static String getId() {return id;}

    public static Context getAppContext() {return context;}
}
