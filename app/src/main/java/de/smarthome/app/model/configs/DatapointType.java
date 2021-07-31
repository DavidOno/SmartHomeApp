package de.smarthome.app.model.configs;

/**
 * This enum lists all the different types for a GIRA-datapoint
 */
public enum DatapointType {

    BINARY, PERCENT, INTEGER, FLOAT, STRING, BYTE;

    /**
     * Converts the type given as string to the corresponding enum.
     * @param type Type that should be converted.
     * @return the corresponding enum-type.
     */
    public static DatapointType convert(String type){
        switch (type.toLowerCase()){
            case "binary": return BINARY;
            case "percent": return PERCENT;
            case "integer":
            case "dword":
                return INTEGER;
            case "float": return FLOAT;
            case "string": return STRING;
            case "byte": return BYTE;
            default: throw new IllegalArgumentException("Type: "+type.toLowerCase()+" is unknown");
        }
    }
}
