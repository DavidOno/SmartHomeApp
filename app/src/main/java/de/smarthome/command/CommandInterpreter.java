package de.smarthome.command;

import java.util.UUID;

public interface CommandInterpreter{

    public Request buildRegisterClientRequest();
    public Request buildUnregisterClientRequest();
    public Request buildChangeValueCommand();
    public Request buildGetValueCommand(UUID id);
    public Request buildSynchronizeRequest();

}
