package com.example.sombra.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Samuel on 2017-12-18.
 */

public class Utils {
    public static boolean checkString(Context c, String s, String cs) {
        Pattern pattern = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\_^]");
        Matcher matcher = pattern.matcher(s);
        if(s.isEmpty() || s == null) {
            toast(c, cs + " cannot be empty", "short");
            return false;
        }
        else if(matcher.find()) {
            toast(c, cs + " cannot contain [~#@*+%{}<>[]|\"_^!?]", "short");
            return false;
        }
        else {
            return true;
        }

    }

    public static void toast(Context c, String s, String length) {
        if(length == "short") {
            Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(c, s, Toast.LENGTH_LONG).show();
        }
    }
}
