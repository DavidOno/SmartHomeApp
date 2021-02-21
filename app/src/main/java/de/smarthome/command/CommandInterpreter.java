package de.smarthome.command;


import java.util.function.Consumer;

import de.smarthome.model.impl.UIConfig;

public interface CommandInterpreter{

    Request buildRegisterClientRequest(String username, String pwd);
    Request buildUnregisterClientRequest();
    Request buildChangeValueCommand(String id, Integer value);
    Request buildGetValueCommand(String id);
    Request buildUIConfigRequest();
    Request buildAvailabilityCheckRequest();
    Request buildUnregisterUserRequest();
    Request buildGetAllValuesRequest(String locationID, UIConfig uiConfig);
    void setIP(String ip);
    void setToken(String token);
    void buildRegisterCallbackCommand(String ip, Consumer<Request> callback);
    void buildUnRegisterCallbackCommand(String ip, Consumer<Request> callback);
}
