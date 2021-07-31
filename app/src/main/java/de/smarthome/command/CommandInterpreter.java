package de.smarthome.command;


import java.util.function.Consumer;

import de.smarthome.app.model.configs.AdditionalConfig;

/**
 * This interface specifies the behavior of the commandInterpreter.
 * The commandInterpreter will be tasked to build a certain request
 * for a specific implementation of the GIRA-API.
 */
public interface CommandInterpreter{

    /**
     * Builds the request for checking the availability of the GIRA-server.
     * @return Complete request for availability.
     */
    Request buildAvailabilityCheckRequest();

    /**
     * Builds the request for registering a user to the GIRA-server.
     * @param username Username of the user.
     * @param pwd Password of the user.
     * @return Complete request for register.
     */
    Request buildRegisterClientRequest(String username, String pwd);

    /**
     * Builds the request for unregistering a user to the GIRA-server.
     * @return Complete request for unregister.
     */
    Request buildUnregisterClientRequest();

    /**
     * Builds the request for changing/updating a value of a certain device, like light on/off.
     * @param id ID of the device.
     * @param value The new value.
     * @return Complete request for changing the value.
     */
    Request buildChangeValueCommand(String id, Object value);

    /**
     * Builds the request for getting information/value about from a certain device, like temperature.
     * @param id The ID of the device.
     * @return Complete request for getting the information.
     */
    Request buildGetValueCommand(String id);

    /**
     * Builds the request for getting the ui-config from the GIRA-server.
     * @return Complete request for the ui-config.
     */
    Request buildUIConfigRequest();

    /**
     * Builds the request registering the callbackserver at the GIRA-server.
     * @param ipCallbackServer IP of the callback-server.
     * @return Complete request for registering the callback-server.
     */
    Request buildRegisterCallbackServerAtGiraServer(String ipCallbackServer);

    /**
     * Builds the request for unregistering the callbackserver from the GIRA-server.
     * @return Complete request for unregister.
     */
    Request buildUnRegisterCallbackServerAtGiraServer();

    /**
     * Builds the request for additional configs. Normally from the callbackserver.
     * @param ip IP of the server.
     * @param additionalConfig Specifies which additional config.
     * @return Complete request for additional configs.
     */
    Request buildAdditionalConfigRequest(String ip, AdditionalConfig additionalConfig);

    void setIP(String ip);
    void setToken(String token);

    /**
     * Builds the request for registering a user to the callback-server.
     * @param ip IP of the callback-server.
     * @param callback The callback, since it's an asynchronous method.
     * @return Complete request for register.
     */
    void buildRegisterAtCallbackServerCommand(String ip, Consumer<Request> callback);

    /**
     * Builds the request for unregistering a user to the callback-server.
     * @param ip IP of the callback-server.
     * @param callback The callback, since it's an asynchronous method.
     * @return Complete request for unregister.
     */
    void buildUnRegisterAtCallbackServerCommand(String ip, Consumer<Request> callback);

}
