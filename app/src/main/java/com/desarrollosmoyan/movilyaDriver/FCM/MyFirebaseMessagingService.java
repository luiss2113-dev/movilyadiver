package com.desarrollosmoyan.movilyaDriver.FCM;


import android.content.Context;
import android.content.Intent;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.desarrollosmoyan.movilyaDriver.Activity.MainActivity;
import com.desarrollosmoyan.movilyaDriver.Helper.SharedHelper;


import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgingService";
    private static final String TITLE = "title";
    private static final String EMPTY = "";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String ACTION = "action";
    private static final String DATA = "data";
    private static final String ACTION_DESTINATION = "action_destination";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            String myCustomKey = data.get("message");
            if(myCustomKey.equalsIgnoreCase("New Incoming Ride")){
                Log.v("Message data payload:", "Arrived");
                Context context = this;
                Intent launchIntent = context.getPackageManager().
                        getLaunchIntentForPackage(getPackageName());
                launchIntent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(launchIntent);
            }
            Log.d(TAG, "Message data payload: " + myCustomKey);
            handleData(data);

        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification());
        }// Check if message contains a notification payload.

    }

    private void handleNotification(RemoteMessage.Notification RemoteMsgNotification) {
        String message = RemoteMsgNotification.getBody();
        String title = RemoteMsgNotification.getTitle();
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();
    }

    private void handleData(Map<String, String> data) {
        String title = data.get(TITLE);
        String message = data.get(MESSAGE);
        String iconUrl = data.get(IMAGE);
        String action = data.get(ACTION);
        String actionDestination = data.get(ACTION_DESTINATION);
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        notificationVO.setIconUrl(iconUrl);
        notificationVO.setAction(action);
        notificationVO.setActionDestination(actionDestination);

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        SharedHelper.putKey(getApplicationContext(), "device_token",""+ s);
    }
}
//package com.nourdelivery.gruasdriver.FCM;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.content.ContextCompat;
//
//import com.nourdelivery.gruasdriver.Helper.SharedHelper;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.nourdelivery.gruasdriver.Activity.MainActivity;
//import com.nourdelivery.gruasdriver.R;
//import com.nourdelivery.gruasdriver.Utilities.Utilities;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//
//    private static final String TAG = "MyFirebaseMsgService";
//    Utilities utils = new Utilities();
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        if (remoteMessage.getData() != null) {
//            utils.print(TAG, "From: " + remoteMessage.getFrom());
//            utils.print(TAG, "Notification Message Body: " + remoteMessage.getData());
//            //Calling method to generate notification
//            sendNotification(remoteMessage.getData().get("message"));
//        }else{
//            utils.print(TAG,"FCM Notification failed");
//        }
//    }
//
//    //This method is only generating push notification
//    //It is same as we did in earlier posts
//    private void sendNotification(String messageBody) {
//
//        if (!Utilities.isAppIsInBackground(getApplicationContext())) {
//            // app is in foreground, broadcast the push message
//            utils.print(TAG,"foreground");
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.putExtra("push", true);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                        PendingIntent.FLAG_ONE_SHOT);
//            long[] VIBRATE_PATTERN    = {0, 500};
//
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                        .setContentTitle(getString(R.string.app_name))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_tone))
//                        .setVibrate(VIBRATE_PATTERN)
//                        .setContentIntent(pendingIntent);
//
//                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
//
//
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                notificationManager.notify(0, notificationBuilder.build());
//
//
//        }else{
//            utils.print(TAG,"background");
//            // app is in background, show the notification in notification tray
//            if(messageBody.equalsIgnoreCase("New Incoming Ride")){
//                Intent intent = new Intent(this, MainActivity.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                        PendingIntent.FLAG_ONE_SHOT);
//                long[] VIBRATE_PATTERN    = {0, 500};
//
//                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                        .setContentTitle(getString(R.string.app_name))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setVibrate(VIBRATE_PATTERN)
//                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_tone))
//                        .setContentIntent(pendingIntent);
//
//                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
//
//
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                notificationManager.notify(0, notificationBuilder.build());
//            }else{
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.putExtra("push", true);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                        PendingIntent.FLAG_ONE_SHOT);
//
//                long[] VIBRATE_PATTERN    = {0, 500};
//                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                        .setContentTitle(getString(R.string.app_name))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setVibrate(VIBRATE_PATTERN)
//                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_tone))
//                        .setContentIntent(pendingIntent);
//
//                notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder), 1);
//
//
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                notificationManager.notify(0, notificationBuilder.build());
//            }
//        }
//
//
//    }
//
//    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
//            return R.mipmap.ic_launcher;
//        }else {
//            return R.mipmap.ic_launcher;
//        }
//    }
//
//
////FOR FUTURE REFERENCE
//
////    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
////
////    private NotificationUtils notificationUtils;
////
////    @Override
////    public void onMessageReceived(RemoteMessage remoteMessage) {
////        utils.print(TAG, "From: " + remoteMessage.getFrom());
////
////        if (remoteMessage == null)
////            return;
////
////        // Check if message contains a notification payload.
////        if (remoteMessage.getNotification() != null) {
////            utils.print(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
////            handleNotification(remoteMessage.getNotification().getBody());
////        }
////
////        // Check if message contains a data payload.
////        if (remoteMessage.getData().size() > 0) {
////            utils.print(TAG, "Data Payload: " + remoteMessage.getData().toString());
////
////            try {
////                JSONObject json = new JSONObject(remoteMessage.getData().toString());
////                handleDataMessage(json);
////            } catch (Exception e) {
////                utils.print(TAG, "Exception: " + e.getMessage());
////            }
////        }
////    }
////
////    private void handleNotification(String message) {
////        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
////            // app is in foreground, broadcast the push message
////            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
////            pushNotification.putExtra("message", message);
////            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
////
////            // play notification sound
////            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
////            notificationUtils.playNotificationSound();
////        }else{
////            // If the app is in background, firebase itself handles the notification
////        }
////    }
////
////    private void handleDataMessage(JSONObject json) {
////        utils.print(TAG, "push json: " + json.toString());
////
////        try {
////            JSONObject data = json.getJSONObject("data");
////
////            String title = data.getString("title");
////            String message = data.getString("message");
////            boolean isBackground = data.getBoolean("is_background");
////            String imageUrl = data.getString("image");
////            String timestamp = data.getString("timestamp");
////            JSONObject payload = data.getJSONObject("payload");
////
////            utils.print(TAG, "title: " + title);
////            utils.print(TAG, "message: " + message);
////            utils.print(TAG, "isBackground: " + isBackground);
////            utils.print(TAG, "payload: " + payload.toString());
////            utils.print(TAG, "imageUrl: " + imageUrl);
////            utils.print(TAG, "timestamp: " + timestamp);
////
////
////            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
////                // app is in foreground, broadcast the push message
////                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
////                pushNotification.putExtra("message", message);
////                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
////
////                // play notification sound
////                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
////                notificationUtils.playNotificationSound();
////            } else {
////                // app is in background, show the notification in notification tray
////                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
////                resultIntent.putExtra("message", message);
////
////                // check for image attachment
////                if (TextUtils.isEmpty(imageUrl)) {
////                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
////                } else {
////                    // image is present, show notification with image
////                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
////                }
////            }
////        } catch (JSONException e) {
////            utils.print(TAG, "Json Exception: " + e.getMessage());
////        } catch (Exception e) {
////            utils.print(TAG, "Exception: " + e.getMessage());
////        }
////    }
////
////    /**
////     * Showing notification with text only
////     */
////    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
////        notificationUtils = new NotificationUtils(context);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
////    }
////
////    /**
////     * Showing notification with text and image
////     */
////    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
////        notificationUtils = new NotificationUtils(context);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
////    }


//    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//
//        SharedHelper.putKey(getApplicationContext(), "device_token",""+ s);
//
//    }
//}
