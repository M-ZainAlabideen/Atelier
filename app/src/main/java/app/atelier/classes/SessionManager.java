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
    private static final String AS_GUEST = "continue_as_guest";
    private static final String LANGUAGE_CODE = "language_code";
    private static final String CURRENCY_CODE = "currency_name";

    private static String IsNotificationOn = "IsNotificationOn";
    public static final String KEY_RegID = "regId";

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

    public void setUserId(String userId) {
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPref.getString(USER_ID, "");
    }

    public void setUserName(String userName) {
        editor.putString(USER_NAME, userName);
        editor.apply();
    }

    public String getUserName() {
        return sharedPref.getString(USER_NAME, "");
    }


    public void setFirstName(String firstName) {
        editor.putString(FIRST_NAME, firstName);
        editor.apply();
    }

    public String getFirstName() {
        return sharedPref.getString(FIRST_NAME, "");
    }

    public void setLastName(String lastName) {
        editor.putString(LAST_NAME, lastName);
        editor.apply();
    }

    public String getLastName() {
        return sharedPref.getString(LAST_NAME, "");
    }

    public void setPhone(String phone) {
        editor.putString(PHONE, phone);
        editor.apply();
    }

    public String getPhone() {
        return sharedPref.getString(PHONE, "");
    }

    public void setEmail(String email) {
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public String getEmail() {
        return sharedPref.getString(EMAIL, "");
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

    public void changeNotification(boolean status){
        editor.putBoolean(IsNotificationOn,status);
        editor.commit();
    }
    public boolean isNotificationOn(){
        return  sharedPref.getBoolean(IsNotificationOn,true);
    }

    public String getRegId() {
        return sharedPref.getString(KEY_RegID, "");
    }

    public void setRegId(String id) {
        editor.putString(KEY_RegID, id);
        editor.commit();
    }
}
