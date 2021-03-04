package de.smarthome.command;


import java.util.function.Consumer;

public interface CommandInterpreter{

    Request buildAvailabilityCheckRequest();
    Request buildRegisterClientRequest(String username, String pwd);
    Request buildUnregisterClientRequest();
    Request buildChangeValueCommand(String id, Integer value);
    Request buildGetValueCommand(String id);
    Request buildUIConfigRequest();
    Request buildRegisterCallbackServerAtGiraServer(String ipCallbackServer);
    Request buildUnRegisterCallbackServerAtGiraServer();
    void setIP(String ip);
    void setToken(String token);
    void buildRegisterAtCallbackServerCommand(String ip, Consumer<Request> callback);
    void buildUnRegisterAtCallbackServerCommand(String ip, Consumer<Request> callback);

}
