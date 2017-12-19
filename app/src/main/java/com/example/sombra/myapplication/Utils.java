package com.example.sombra.myapplication;

import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Samuel on 2017-12-18.
 */
public class Utils {
    public static boolean checkString(String s, String cs, int min, int max) {
        Pattern pattern = Pattern.compile("[(~#@*+%{}<>\\[\\]|\"\\_^öäå)]");
        Matcher matcher = pattern.matcher(s);
        if (s.isEmpty() || s == null) {
            toast(cs + " cannot be empty", "short");
            return false;
        } else if (matcher.find()) {
            toast(cs + " cannot contain [~#@*+%{}<>[]|\"_^!?öäå]", "short");
            return false;
        } else if (s.length() < min || s.length() > max && min != 0 && max != 0) {
            toast(cs + "cannot be short than " + min + " or longer than " + max, "short");
        } else {
            return true;
        }
        return false;
    }
    public static void toast(String s, String length) {
        if(length == "short") {
            Toast.makeText(UserSingleton.getAppContext(), s, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(UserSingleton.getAppContext(), s, Toast.LENGTH_LONG).show();
        }
    }
}
