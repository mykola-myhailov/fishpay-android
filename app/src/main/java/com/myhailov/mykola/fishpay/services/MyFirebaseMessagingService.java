package com.myhailov.mykola.fishpay.services;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.myhailov.mykola.fishpay.R;
import com.myhailov.mykola.fishpay.activities.ProfileSettingsActivity;
import com.myhailov.mykola.fishpay.activities.login.BeginActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mykola Myhailov  on 20.02.18.
 */


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    String type = "";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleNow();
            type = "json";
//            sendNotification(remoteMessage.getData().toString());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            type = "message";
//            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }



    private void sendNotification(String messageBody){
        String id="",message="",title="";

        if(type.equals("json")) {
            try {
                JSONObject jsonObject = new JSONObject(messageBody);
                id = jsonObject.getString("id");
                message = jsonObject.getString("message");
                title = jsonObject.getString("title");

            } catch (JSONException e) {
                //            }
            }
        }
        else if(type.equals("message"))
        {
            message = messageBody;
        }

        Intent intent=new Intent(this,ProfileSettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(getString(R.string.app_name));
        notificationBuilder.setContentText(message);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(soundUri);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_launcher_new));
        notificationBuilder.setAutoCancel(true);
        // TODO: 12.07.2018  get Permission

//        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
//        v.vibrate(1000);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }

}



