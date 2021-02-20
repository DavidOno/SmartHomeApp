package de.smarthome.command;


import java.util.List;
import java.util.function.Consumer;

public interface CommandInterpreter{

    Request buildRegisterClientRequest(String username, String pwd);
    Request buildUnregisterClientRequest();
    Request buildChangeValueCommand(String id, Integer value);
    Request buildGetValueCommand(String id);
    Request buildUIConfigRequest();
    Request buildAvailabilityCheckRequest();
    Request buildUnregisterUserRequest();
    Request buildSynchronizeRequest();
    void setIP(String ip);
    void setToken(String token);
    void buildRegisterCallbackCommand(String ip, Consumer<List<Request>> callback);
}
