package app.atelier.pushnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import app.atelier.MainActivity;
import app.atelier.R;
import app.atelier.classes.Constants;
import app.atelier.classes.NotificationUtils;
import app.atelier.classes.SessionManager;

/**
 * Created by DELL on 17-Nov-16.
 */

public class MyFcmListenerService extends FirebaseMessagingService {

    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d("onMessageReceived1", "Notification Message Body: " +
                remoteMessage.getData() + "");
        Log.d("onMessageReceived2", "From: " + remoteMessage.getFrom());
        Log.d("onMessageReceived3", "id: " + remoteMessage.getData().get("I"));
        Log.d("onMessageReceived3", "type: " + remoteMessage.getData().get("T"));
        Log.d("onMessageReceived3", "message: " + remoteMessage.getData().get("message"));
        Id = remoteMessage.getData().get("I");
        type = remoteMessage.getData().get("T");
        // isSpecial = remoteMessage.getData().values().toArray()[0].toString();
        Log.d("onMessageReceived3", "message: " + remoteMessage.getData().get("message"));
        Map data = remoteMessage.getData();

        Log.d("onMessageReceived3", "From: " + remoteMessage.getData());
        Log.d("onMessageReceived", "Notification Message Body: " +
                remoteMessage.getData());
        Log.d("onMessageReceived", "Notification Message Body: " +
                remoteMessage.toString());

        //sendNotification(remoteMessage.getData().get("message"));

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Log.e("onMessageReceived", "Data Payload: " + remoteMessage.getData().toString());

            if (new SessionManager(this).isNotificationOn()) {

                handleDataMessage(remoteMessage);

            }

        }

    }
    // [END receive_message]


    String Id = "", type = "";

    public static int notificationID = 0;

    NotificationUtils notificationUtils;


    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences shared = getSharedPreferences("counter", 0);

        notificationID = Integer.parseInt(shared.getString("cc", "0"));

        PendingIntent contentIntent;

        contentIntent = PendingIntent.getActivity(this, notificationID, new Intent(this,
                MainActivity.class).putExtra("id", Id).putExtra("type", type), PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription(msg);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.launcher_icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setTicker(msg).setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(msg);

        /*..if (sound.equalsIgnoreCase("1")) {

            Uri alarmSound = Uri.parse("android.resource://" + getPackageName() + "/raw/android_notification");

            Log.i("UriUri", "android.resource://" + getPackageName() + "/raw/android_notification");

            mBuilder.setSound(alarmSound);
        }*/

        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        mBuilder.setContentIntent(contentIntent);

        SharedPreferences shared_noti = getSharedPreferences("gpfshared", 0);

        mNotificationManager.notify(notificationID, mBuilder.build());

    }


    private void handleNotification(String message) {

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);

            pushNotification.putExtra("message", message);

            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());

            notificationUtils.playNotificationSound();

        } else {

            // If the app is in background, firebase itself handles the notification

        }

    }


    private void handleDataMessage(RemoteMessage data) {

        Log.e("handleDataMessage", "push json: " + data.toString());

        String type = data.getData().get("T") + "";

        String Id = data.getData().get("I") + "";

        String message = data.getData().get("message");

        //boolean isBackground = data.getBoolean("is_background");

        String imageUrl = "";

        if (data.getData().get("U").length() > 0) {

            imageUrl = Constants.CMS_NOTIFICATION_IMAGE_URL + data.getData().get("U");

        }

        String timestamp = data.getData().get("time");

        Log.e("handleDataMessage", "message: " + message);

        //Log.e("handleDataMessage", "isBackground: " + isBackground);

        Log.e("handleDataMessage", "imageUrl: " + imageUrl);

        Log.e("handleDataMessage", "timestamp: " + timestamp);


        Log.d("isAppIsInBackground", "true");

        // app is in background, show the notification in notification tray
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        resultIntent.putExtra("message", message);

        resultIntent.putExtra("type", type);

        if (Id != null && Id.length() > 0)
            resultIntent.putExtra("Id", Id);

        // check for image attachment
        if (TextUtils.isEmpty(imageUrl)) {

            showNotificationMessage(getApplicationContext(), getString(R.string.app_name), message, timestamp, resultIntent);

        } else {

            // image is present, show notification with image
            showNotificationMessageWithBigImage(getApplicationContext(), getString(R.string.app_name), message, timestamp, resultIntent, imageUrl);

        }

    }


    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);

    }


    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {

        Log.d("notificationUtils", "1");

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);

    }


}