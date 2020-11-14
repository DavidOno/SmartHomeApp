package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * If the PigmentContent is -1, then colored light is not supported
 *
 * @author David
 */

public class LightAdjustCommand implements Command {

    private double intensity;
    private double redPigmentContent;
    private double bluePigmentContent;
    private double greenPigmentContent;
    private double whitePigmentContent;
    private String ID;

    public LightAdjustCommand(double intensity, String ID){
        this(intensity, -1, -1, -1, -1, ID);
    }

    public LightAdjustCommand(double intensity, double redPigmentContent, double bluePigmentContent, double greenPigmentContent, double whitePigmentContent, String ID) {
        this.intensity = intensity;
        this.redPigmentContent = redPigmentContent;
        this.bluePigmentContent = bluePigmentContent;
        this.greenPigmentContent = greenPigmentContent;
        this.whitePigmentContent = whitePigmentContent;
        this.ID = ID;
    }

    public double getIntensity() {
        return intensity;
    }

    public double getRedPigmentContent() {
        return redPigmentContent;
    }

    public double getBluePigmentContent() {
        return bluePigmentContent;
    }

    public double getGreenPigmentContent() {
        return greenPigmentContent;
    }

    public double getWhitePigmentContent() {
        return whitePigmentContent;
    }

    public String getID() {
        return ID;
    }

    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
       return commandInterpreter.buildAdjustLightRequest();
    }
}
