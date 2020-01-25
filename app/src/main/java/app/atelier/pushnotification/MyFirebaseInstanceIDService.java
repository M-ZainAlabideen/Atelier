package app.atelier.pushnotification;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import app.atelier.classes.AppController;
import app.atelier.classes.Constants;
import app.atelier.classes.SessionManager;
import app.atelier.webservices.AtelierApiConfig;
import app.atelier.webservices.AtelierApiInterface;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

/**
 * Created by DELL on 17-Nov-16.
 */

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    SessionManager sessionManager;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d("Refreshed", "Refreshed token: " + s);

        sessionManager = new SessionManager(this);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(s);

    }

    private void sendRegistrationToServer(final String regid) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                //try {
                    /*if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);*/
                //regid = FirebaseInstanceId.getInstance().getToken();
                msg = "Device registered, registration ID=" + regid;

                //} catch (IOException ex) {
                //msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
                //}
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.e("registrationId service", "regId -> "+regid +"------------"+ sessionManager.getUserId());

                AtelierApiConfig.getCallingAPIInterface().insertToken(Constants.AUTHORIZATION_VALUE, regid, "2", AppController.getInstance().getIMEI(), sessionManager.getUserId().length()>0 ? sessionManager.getUserId() : "0", new Callback<Response>() {
                    @Override
                    public void success(Response s, Response response) {

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
        }.execute(null, null, null);
    }


}
