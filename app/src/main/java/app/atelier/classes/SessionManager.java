package app.atelier.classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SessionManager {
    Context context;
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    public static final String USER_PREF = "user-pref";
    private static final String IS_LOGGED = "isLogged";
    private static final String USER_ID = "id";
    private static final String USER_NAME = "userName";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String AS_GUEST = "continue_as_guest";
    private static final String LANGUAGE_CODE = "language_code";
    private static final String CURRENCY_CODE = "currency_name";



    public SessionManager(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(USER_PREF,MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void LoginSession(){
        editor.putBoolean(IS_LOGGED,true);
        editor.commit();
    }
    public boolean isLoggedIn(){
        return  sharedPref.getBoolean(IS_LOGGED,false);
    }

    public void logout(){
        editor.putBoolean(IS_LOGGED,false);
        editor.commit();
    }

    public void setUser(int id, String userName, String firstName, String lastName, String phone, String email, String password) {
        editor.putString(USER_ID, String.valueOf(id));
        editor.putString(USER_NAME, userName);
        editor.putString(FIRST_NAME, firstName);
        editor.putString(LAST_NAME, lastName);
        editor.putString(PHONE, phone);
        editor.putString(EMAIL, email);
        editor.putString(PASSWORD, password);
        editor.apply();

    }

    public Map<String, String> getUser() {
        Map<String, String> userMap = new HashMap<>();
        userMap.put(USER_ID, sharedPref.getString(USER_ID, ""));
        userMap.put(USER_NAME, sharedPref.getString(USER_NAME, ""));
        userMap.put(FIRST_NAME, sharedPref.getString(FIRST_NAME, ""));
        userMap.put(LAST_NAME, sharedPref.getString(LAST_NAME, ""));
        userMap.put(PHONE, sharedPref.getString(PHONE, ""));
        userMap.put(EMAIL, sharedPref.getString(EMAIL, ""));
        userMap.put(PASSWORD, sharedPref.getString(PASSWORD, ""));

        return userMap;
    }

    public String getUserId() {
        return sharedPref.getString(USER_ID, "");
    }

    public void setUserLanguage(String languageCode) {
        editor.putString(LANGUAGE_CODE, languageCode);
        editor.apply();

    }

    public String getUserLanguage() {
        return sharedPref.getString(LANGUAGE_CODE, "");
    }

    public void ContinueAsGuest() {
        editor.putBoolean(AS_GUEST, true);
        editor.apply();
    }
    public void guestLogout() {
        editor.putBoolean(AS_GUEST, false);
        editor.apply();
    }

    public boolean isGuest() {
        return sharedPref.getBoolean(AS_GUEST, false);
    }


    public void setCurrencyCode(String currencyCode) {
        editor.putString(CURRENCY_CODE,currencyCode);
        editor.apply();

    }

    public String getCurrencyCode() {
        return sharedPref.getString(CURRENCY_CODE, "");
    }
}
