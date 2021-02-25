package de.smarthome.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceEvent {

    private final Enum serviceEventEnum;

    public ServiceEvent(@JsonProperty("event") String event){
        serviceEventEnum = string2Enum(event);
    }

    private Enum string2Enum(String event){
        switch (event){
            case "restart": return Enum.RESTART;
            case "startup": return Enum.STARTUP;
            case "projectConfigChanged": return Enum.PROJECT_CONFIG_CHANGED;
            case "uiConfigChanged": return Enum.UI_CONFIG_CHANGED;
            default: throw new IllegalArgumentException(event+" is an unknown event");
        }
    }

    public Enum getEvent(){
        return serviceEventEnum;
    }

    public enum Enum {
        PROJECT_CONFIG_CHANGED, UI_CONFIG_CHANGED, RESTART, STARTUP;
    }
}
