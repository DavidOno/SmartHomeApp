package de.smarthome.model.configs;

public enum DatapointType {

    BINARY, PERCENT, INTEGER, FLOAT, STRING, BYTE;

    public static DatapointType convert(String type){
        switch (type.toLowerCase()){
            case "binary": return BINARY;
            case "percent": return PERCENT;
            case "integer": return INTEGER;
            case "float": return FLOAT;
            case "string": return STRING;
            case "byte": return BYTE;
            default: throw new IllegalArgumentException("Type: "+type.toLowerCase()+" is unknown");
        }
    }
}
