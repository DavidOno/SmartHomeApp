package de.smarthome.command.impl;

import de.smarthome.app.model.configs.AdditionalConfig;
import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class AdditionalConfigCommand implements Command {

    private final AdditionalConfig config;
    private final String ip;

    public AdditionalConfigCommand(String ip, AdditionalConfig config) {
        this.config = config;
        this.ip = ip;
    }

    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildAdditionalConfigRequest(ip, config);
    }
}
