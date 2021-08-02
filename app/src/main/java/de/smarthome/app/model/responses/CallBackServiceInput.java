package de.smarthome.app.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores stores all information given by GIRA when the callback-event-loop detects an event.
 */
public class CallBackServiceInput {

    private final int failures;
    private final String token;
    private final List<ServiceEvent> serviceEvents;

    public CallBackServiceInput(@JsonProperty("failure") int failures, @JsonProperty("token") String token, @JsonProperty("events") List<ServiceEvent> serviceEvents) {
        this.failures = failures;
        this.token = token;
        this.serviceEvents = new ArrayList<>(serviceEvents);
    }

    public int getFailures() {
        return failures;
    }

    public String getToken() {
        return token;
    }

    public List<ServiceEvent> getServiceEvents() {
        return serviceEvents;
    }

    @Override
    public String toString() {
        return "CallbackInput [failures=" + failures + ", token=" + token + ", events=" + serviceEvents + "]";
    }
}
