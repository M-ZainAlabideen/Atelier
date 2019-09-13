package app.atelier.classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SessionManager {
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    public static void setUser(Context context, int id, String userName, String firstName, String lastName, String phone, String email, String password) {
        preferences = context.getSharedPreferences(Constants.USER_PREF, MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("id", String.valueOf(id));
        editor.putString("userName", userName);
        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();

    }

    public static Map<String, String> getUser(Context context) {
        Map<String, String> userMap = new HashMap<>();
        preferences = context.getSharedPreferences(Constants.USER_PREF, MODE_PRIVATE);
        userMap.put("id", preferences.getString("id", ""));
        userMap.put("userName", preferences.getString("userName", ""));
        userMap.put("firstName", preferences.getString("firstName", ""));
        userMap.put("lastName", preferences.getString("lastName", ""));
        userMap.put("phone", preferences.getString("phone", ""));
        userMap.put("email", preferences.getString("email", ""));
        userMap.put("password", preferences.getString("password", ""));

        return userMap;
    }

    public static String getUserId(Context context) {
        preferences = context.getSharedPreferences(Constants.USER_PREF, MODE_PRIVATE);
        return preferences.getString("id", "");
    }

    public static void setUserLanguage(Context context, String languageCode) {
        preferences = context.getSharedPreferences(Constants.USER_PREF, MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("language_code", languageCode);
        editor.apply();

    }

    public static String getUserLanguage(Context context) {
        preferences = context.getSharedPreferences(Constants.USER_PREF, MODE_PRIVATE);
        return preferences.getString("language_code", "");
    }

    public static void skip(Context context) {
        preferences = context.getSharedPreferences(Constants.SKIP_PREF, MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("skip", true);
        editor.apply();
    }

    public static boolean checkSkip(Context context) {
        preferences = context.getSharedPreferences(Constants.SKIP_PREF, MODE_PRIVATE);
        return preferences.getBoolean("skip", false);
    }
}
