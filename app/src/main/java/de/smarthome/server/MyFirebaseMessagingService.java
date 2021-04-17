package de.smarthome.server;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import de.smarthome.app.model.responses.CallbackValueInput;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";
    private static final CallbackObserver valueObserver = new CallbackObserver();
    private static final CallbackObserver serviceObserver = new CallbackObserver();

    public static CallbackObserver getValueObserver(){
        return valueObserver;
    }

    public static CallbackObserver getServiceObserver(){
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
            Log.d(TAG, "This notification is currently not forwarded to anyone");
        }
    }

    private void handleDataPayLoad(RemoteMessage remoteMessage) {
        if (hasData(remoteMessage)) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            ObjectMapper m = new ObjectMapper();
            CallbackValueInput callbackValueInput = m.convertValue(remoteMessage.getData(), new TypeReference<CallbackValueInput>() {});
            if(callbackValueInput.getValue() != null){
                valueObserver.notify(callbackValueInput);
            }
            if(callbackValueInput.getEvent() != null){
                serviceObserver.notify(callbackValueInput);
            }
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
