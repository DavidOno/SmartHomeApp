package de.smarthome.app.model.responses;

public enum Events {

    PROJECT_CONFIG_CHANGED, UI_CONFIG_CHANGED, RESTART, STARTUP, TEST;

    public static Events convert(String event){
        if(event == null){
            return null;
        }
        switch (event.toLowerCase()){
            case "restart": return RESTART;
            case "startup": return STARTUP;
            case "projectconfigchanged": return PROJECT_CONFIG_CHANGED;
            case "uiconfigchanged": return UI_CONFIG_CHANGED;
            case "test": return TEST;
            default: throw new IllegalArgumentException(event+" is an unknown event");
        }
    }
}
