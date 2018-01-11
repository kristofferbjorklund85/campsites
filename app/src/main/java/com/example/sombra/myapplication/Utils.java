package com.example.sombra.myapplication;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Samuel on 2017-12-18.
 */
public class Utils {
    public static boolean checkString(String s, String type, int min, int max) {
        Pattern pattern = Pattern.compile("[(~#@*+%{}<>\\[\\]|\"_^öäå)']");
        Matcher matcher = pattern.matcher(s);
        if (s.isEmpty() || s == null) {
            toast(type + " cannot be empty", "long");
            return false;
        } else if (matcher.find()) {
            toast(type + " cannot contain [~#@*+%{}<>[]|\"_^!?öäå]", "long");
            return false;
        } else if (s.length() < min && min != 0 ) {
            toast(type + " cannot be short than " + min, "long");
        } else if (s.length() > max && max != 0) {
            toast(type + " or longer than " + max, "long");
        } else {
            return true;
        }
        return false;
    }

    public static void toast(String s, String length) {
        if(length == "short") {
            Toast.makeText(SessionSingleton.getAppContext(), s, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(SessionSingleton.getAppContext(), s, Toast.LENGTH_LONG).show();
        }
    }
}
