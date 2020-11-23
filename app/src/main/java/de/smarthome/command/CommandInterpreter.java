package de.smarthome.command;

import java.util.UUID;

public interface CommandInterpreter{

    public Request buildRegisterClientRequest(String username, String pwd);
    public Request buildUnregisterClientRequest();
    public Request buildChangeValueCommand(UUID id, Integer value);
    public Request buildGetValueCommand(UUID id);
    public Request buildUIConfigRequest();
    public Request buildAvailabilityCheckRequest();
    public Request buildSynchronizeRequest();
    public void setIP(String ip);
    public void setToken(String token);

}
