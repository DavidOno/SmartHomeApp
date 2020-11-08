package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class BlindAdjustCommand implements Command {

    private double degree;
    private String ID;

    public BlindAdjustCommand(double degree, String ID) {
        this.degree = degree;
        this.ID = ID;
    }

    public double getDegree() {
        return degree;
    }

    public String getID() {
        return ID;
    }

    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildAdjustBlindRequest();
    }
}
