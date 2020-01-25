package app.atelier;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import app.atelier.classes.Constants;
import app.atelier.classes.GlobalFunctions;
import app.atelier.classes.LocaleHelper;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.responses.stores.GetStores;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
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

}
