package de.smarthome.model.configs;

public enum DatapointAccess {

    READ, WRITE, READ_WRITE;

    public static DatapointAccess convert(String access){
        String reducedAccess = access.toLowerCase().replace("e", "");
        switch (reducedAccess){
            case "r": return READ;
            case "w": return WRITE;
            case "rw": return READ_WRITE;
            default: throw new IllegalArgumentException("Access: "+access+"/"+reducedAccess+" unknown");
        }
    }
}
