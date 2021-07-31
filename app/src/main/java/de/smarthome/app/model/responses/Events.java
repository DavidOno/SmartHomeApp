package de.smarthome.app.model.responses;

/**
 * This enum lists all possible events, that can happen at the GIRA-server.
 */
public enum Events {

    PROJECT_CONFIG_CHANGED, UI_CONFIG_CHANGED, RESTART, STARTUP, TEST;

    /**
     * Converts the event given as string to the corresponding enum.
     * @param event Event that should be converted.
     * @return the corresponding enum-type.
     */
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
