package com.example.sombra.myapplication;

import android.content.Context;

/**
 * Created by Sombra on 2017-11-27.
 */

public class SessionSingleton {
    private static String username = "guest";
    private static String id = null;
    private static Context context;
    private static boolean promptLogin = false;
    private static String url;

    public static void setUsername(String user) {
        SessionSingleton.username = user;}

    public static void setId(String id) {
        SessionSingleton.id = id;}

    public static void setAppContext(Context context) {
        SessionSingleton.context = context;}

    public static void setPromptLogin(boolean b) {
        promptLogin = b;
    }

    public static void setURL(String s) {url = s;}

    public static String getUsername() {return username;}

    public static String getId() {return id;}

    public static Context getAppContext() {return context;}

    public static boolean getPromptLogin() {return promptLogin;}

    public static String getURL() {return url;}
}
