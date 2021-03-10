package de.smarthome.command.impl;

import java.util.Arrays;
import java.util.List;

import de.smarthome.command.AdditionalConfigs;
import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class AdditionalConfigCommand implements Command {

    private final AdditionalConfigs config;
    private final String ip;

    public AdditionalConfigCommand(String ip, AdditionalConfigs config) {
        this.config = config;
        this.ip = ip;
    }

    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildAdditionalConfigRequest(ip, config);
    }
}
