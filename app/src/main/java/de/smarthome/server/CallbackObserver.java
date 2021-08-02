package de.smarthome.server;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.smarthome.app.model.responses.CallbackValueInput;

/**
 * This class allows to register for any callbacks from the GIRA-Server callback-loop.
 * This callback-loop keeps track of changes and events, e.g.: server restart.
 * Every received callback will be forwarded to the subscribers.
 */
public class CallbackObserver {

    private static final String TAG = "CallbackObserver";

    private final List<CallbackSubscriber> subscribers = new ArrayList<>();

    /**
     * Adds the subscriber to the list of subscribers.
     * @param subscriber Subscriber to be added.
     */
    public void subscribe(CallbackSubscriber subscriber){
        subscribers.add(subscriber);
    }

    /**
     * Removes the subscriber from the list of subscribers.
     * @param subscriber Subscriber to be removed.
     */
    public void unsubscribe(CallbackSubscriber subscriber){
        subscribers.remove(subscriber);
    }

    /**
     * Notifies all subscribers about the received callback.
     * @param input Received callback.
     */
    public void notify(CallbackValueInput input){
        Log.d(TAG, input.toString());
        subscribers.forEach(s -> s.update(input));
    }
}
