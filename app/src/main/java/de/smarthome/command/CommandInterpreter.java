package de.smarthome.command;


public interface CommandInterpreter{

    Request buildRegisterClientRequest(String username, String pwd);
    Request buildUnregisterClientRequest();
    Request buildChangeValueCommand(String id, Integer value);
    Request buildGetValueCommand(String id);
    Request buildUIConfigRequest();
    Request buildAvailabilityCheckRequest();
    Request buildSynchronizeRequest();
    void setIP(String ip);
    void setToken(String token);

}
