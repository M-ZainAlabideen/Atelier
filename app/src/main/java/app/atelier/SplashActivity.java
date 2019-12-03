package app.atelier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import app.atelier.classes.AppController;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.LocaleHelper;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.stores.GetStores;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {
    public SessionManager sessionManager;
    //splash screen displaying time
    private String language;
    private static final int SPLASH_DISPLAY_LENGTH = 3000;
    String regid="";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(LocaleHelper.onAttach(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //make the screen without statusBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(this);
        sessionManager.ContinueAsGuest();
        language = sessionManager.getUserLanguage();
        LocaleHelper.setLocale(SplashActivity.this,language);
        if (language.equals("en")) {
            MainActivity.isEnglish = true;
        } else if (language.equals("ar")) {
            MainActivity.isEnglish = false;

        } else {
            currentStoreApi();
        }
        GlobalFunctions.setUpFont(this);
        //after splash screen >> the main activity will be opened and the main fragment will be displayed(MagalesTypesFragment)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, SPLASH_DISPLAY_LENGTH);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("splash", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        regid = task.getResult().getToken();

                        Log.e("registerationid Splash ", "regid -> "+regid);

                        registerInBackground();


                    }
                });
    }

    public void currentStoreApi() {
        AtelierApiConfig.getCallingAPIInterface().currentStore(Constants.AUTHORIZATION_VALUE, new Callback<GetStores>() {
            @Override
            public void success(GetStores getStores, Response response) {
                int defaultLanguageId = getStores.stores.get(0).defaultLanguageId;
                for (int i = 0; i < getStores.stores.get(0).storeLanguages.size(); i++) {
                    if (defaultLanguageId == getStores.stores.get(0).storeLanguages.get(i).languageId) {
                        language = getStores.stores.get(0).storeLanguages.get(i).languageCode;
                        LocaleHelper.setLocale(SplashActivity.this,language);
                        sessionManager.setUserLanguage(language);
                        if (language.equals("en")) {
                            MainActivity.isEnglish = true;
                        } else if (language.equals("ar")) {
                            MainActivity.isEnglish = false;
                        }
                        GlobalFunctions.setUpFont(SplashActivity.this);
                    }
                }
                sessionManager.setCurrencyCode(getStores.stores.get(0).storeCurrencies.get(0).currencyCode);
            }

            @Override
            public void failure(RetrofitError error) {
                //initialize of language
                language = "ar";
                LocaleHelper.setLocale(SplashActivity.this,language);
                sessionManager.setUserLanguage(language);
                MainActivity.isEnglish = false;
            }
        });
    }

    private void registerInBackground() {

        AtelierApiConfig.getCallingAPIInterface().insertToken(Constants.AUTHORIZATION, regid, "2", AppController.getInstance().getIMEI(), sessionManager.isLoggedIn()? sessionManager.getUserId() : null, new Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response s, retrofit.client.Response response) {

                TypedInput body = response.getBody();

                try {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));

                    StringBuilder out = new StringBuilder();

                    String newLine = System.getProperty("line.separator");

                    String line;

                    while ((line = reader.readLine()) != null) {
                        out.append(line);
                        out.append(newLine);
                    }

                    String outResponse = out.toString();
                    Log.d("outResponse", ""+outResponse);

                    sessionManager.setRegId(regid);



                } catch (Exception ex) {

                    ex.printStackTrace();


                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

}
