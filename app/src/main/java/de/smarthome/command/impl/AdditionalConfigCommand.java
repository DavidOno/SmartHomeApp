package de.smarthome.command.impl;

import de.smarthome.app.model.configs.AdditionalConfig;
import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * With this class a user's intention of getting additional configs can be described.
 */
public class AdditionalConfigCommand implements Command {

    private final AdditionalConfig config;
    private final String ip;

    public AdditionalConfigCommand(String ip, AdditionalConfig config) {
        this.config = config;
        this.ip = ip;
    }

    /*
     * @inheritDoc
     */
    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildAdditionalConfigRequest(ip, config);
    }
}
