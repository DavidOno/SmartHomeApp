package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class TemperatureAdjustCommand implements Command {

    private int temperature;
    private String ID;

    public TemperatureAdjustCommand(int temperature, String ID) {
        this.temperature = temperature;
        this.ID = ID;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getID() {
        return ID;
    }

    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildAdjustTemperatureRequest();
    }
}
