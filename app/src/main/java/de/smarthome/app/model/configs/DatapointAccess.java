package de.smarthome.app.model.configs;

/**
 * This enum lists all of the different posibilities of access for datapoints.
 */
public enum DatapointAccess {

    READ, WRITE, READ_WRITE;

    /**
     * Converts the access given as string to the corresponding enum.
     * @param access Access that should be converted.
     * @return the corresponding enum-type.
     */
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
