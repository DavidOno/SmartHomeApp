package de.smarthome.server;

import java.util.ArrayList;
import java.util.List;

import de.smarthome.model.responses.CallbackInput;

public class CallbackObserver {

    private List<CallbackSubscriber> subscribers = new ArrayList<>();

    public void subscribe(CallbackSubscriber subscriber){
        subscribers.add(subscriber);
    }

    public void unsubscribe(CallbackSubscriber subscriber){
        subscribers.remove(subscriber);
    }

    public void notify(CallbackInput input){
        subscribers.forEach(s -> s.update(input));
    }

}
