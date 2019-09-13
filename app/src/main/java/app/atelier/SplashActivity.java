package app.atelier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import app.atelier.classes.Constants;
import app.atelier.classes.LocaleHelper;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.stores.GetStores;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplashActivity extends AppCompatActivity {
    //splash screen displaying time
    private static final int SPLASH_DISPLAY_LENGTH = 3000;
    //this variable is used to check the current selected language in the app
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //make the screen without statusBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        MainActivity.language = SessionManager.getUserLanguage(this);
        LocaleHelper.setLocale(SplashActivity.this, MainActivity.language);
        if (MainActivity.language.equals("en")) {
            MainActivity.isEnglish = true;
        } else if (MainActivity.language.equals("ar")) {
            MainActivity.isEnglish = false;

        } else {
            currentStoreApi();
        }

        //after splash screen >> the main activity will be opened and the main fragment will be displayed(MagalesTypesFragment)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void currentStoreApi() {
        AtelierApiConfig.getCallingAPIInterface().currentStore(Constants.AUTHORIZATION_VALUE, new Callback<GetStores>() {
            @Override
            public void success(GetStores getStores, Response response) {
                int defaultLanguageId = getStores.stores.get(0).defaultLanguageId;
                for (int i = 0; i < getStores.stores.get(0).storeLanguages.size(); i++) {
                    if (defaultLanguageId == getStores.stores.get(0).storeLanguages.get(i).languageId) {
                        MainActivity.language = getStores.stores.get(0).storeLanguages.get(i).languageCode;
                        LocaleHelper.setLocale(SplashActivity.this, MainActivity.language);
                        SessionManager.setUserLanguage(SplashActivity.this,MainActivity.language);
                        if (MainActivity.language.equals("en")) {
                            MainActivity.isEnglish = true;
                        } else if (MainActivity.language.equals("ar")) {
                            MainActivity.isEnglish = false;
                        }
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
