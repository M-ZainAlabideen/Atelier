package app.atelier.webservices;

import app.atelier.classes.Constants;
import retrofit.RestAdapter;

public class AtelierApiConfig {
    public static AtelierApiInterface apiInterface;

    public static AtelierApiInterface getCallingAPIInterface(){
        if(apiInterface == null){
            try {
                RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                        .setEndpoint(Constants.BASE_URL).build();
                apiInterface = restAdapter.create(AtelierApiInterface.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return apiInterface;
    }
}
