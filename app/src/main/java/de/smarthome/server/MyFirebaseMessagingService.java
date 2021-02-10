package de.smarthome.server;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        handleDataPayLoad(remoteMessage);
        handleNotification(remoteMessage);
    }

    private void handleNotification(RemoteMessage remoteMessage) {
        if (hasNotification(remoteMessage)) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void handleDataPayLoad(RemoteMessage remoteMessage) {
        if (hasData(remoteMessage)) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
    }

    private boolean hasNotification(RemoteMessage remoteMessage) {
        return remoteMessage.getNotification() != null;
    }

    private boolean hasData(RemoteMessage remoteMessage) {
        return remoteMessage.getData().size() > 0;
    }

    //TODO: This can probably be deleted
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "New Token: "+s);
    }
}
