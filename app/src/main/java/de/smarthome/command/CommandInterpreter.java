package de.smarthome.command;

public interface CommandInterpreter{

    public Request buildRegisterClientRequest();
    public Request buildUnregisterClientRequest();
    public Request buildAdjustLightRequest();
    public Request buildAdjustTemperatureRequest();
    public Request buildAdjustBlindRequest();
    public Request buildSynchronizeRequest();

}
