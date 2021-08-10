package de.smarthome.app.repository.responsereactor;

/**
 * Used to represent the current status of the connection to the gira and callbackserver
 */
public enum ServerConnectionEvent {
    CALLBACK_CONNECTION_SUCCESS, CALLBACK_CONNECTION_FAIL, CALLBACK_CONNECTION_ACTIVE,
    GIRA_CONNECTION_SUCCESS, GIRA_CONNECTION_FAIL, GIRA_CONNECTION_ACTIVE;
}
