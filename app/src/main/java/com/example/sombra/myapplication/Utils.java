package com.example.sombra.myapplication;

import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils is our toolbox class. It contains two methods, one for checking the content of strings and
 * one creating a toast for easier syntax and less code.
 */
public class Utils {
    /**
     * ceckString() checks if the content and length of a String is okay for the intented purpose.
     *
     * @param s    The String to be checked.
     * @param type The type of check you're doing, to be able to customize the message saying what's wrong.
     * @param min  Minimum length of the String, make 0 for no minimun length.
     * @param max  Maximum length of the String, make 0 for no maximum length.
     * @return True if okay, false if something is wrong.
     */
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

    /**
     * toast() is our own custom method for creating toasts.
     * It really only saves some code when creating a toast.
     *
     * @param s      The message to be displayed.
     * @param length The length of the toast.
     */
    public static void toast(String s, String length) {
        if(length == "short") {
            Toast.makeText(SessionSingleton.getAppContext(), s, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(SessionSingleton.getAppContext(), s, Toast.LENGTH_LONG).show();
        }
    }
}
