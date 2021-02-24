package de.smarthome.server;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import de.smarthome.model.responses.CallbackInput;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String TAG = "MyFirebaseMessagingService";
    private final CallbackObserver valueObserver = new CallbackObserver();
    private final CallbackObserver serviceObserver = new CallbackObserver();

    public CallbackObserver getValueObserver(){
        return valueObserver;
    }

    public CallbackObserver getServiceObserver(){
        return serviceObserver;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        handleDataPayLoad(remoteMessage);
        handleNotification(remoteMessage);
    }

    private void handleNotification(RemoteMessage remoteMessage) {
        if (hasNotification(remoteMessage)) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //TODO: Add serviceObserver.notify()
        }
    }

    private void handleDataPayLoad(RemoteMessage remoteMessage) {
        if (hasData(remoteMessage)) {
//            try {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//                ObjectMapper m = new ObjectMapper();
//                CallbackInput callbackInput = m.readValue(remoteMessage.getData(), new TypeReference<CallbackInput>() {});
//                valueObserver.notify(callbackInput);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    private boolean hasNotification(RemoteMessage remoteMessage) {
        return remoteMessage.getNotification() != null;
    }

    private boolean hasData(RemoteMessage remoteMessage) {
        return remoteMessage.getData().size() > 0;
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "New Firebase-Token: "+s);
    }
}
