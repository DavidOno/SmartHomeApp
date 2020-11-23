package de.smarthome.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class GetValueCommand implements Command {

    private List<UUID> idOfRequestedValues;

    public GetValueCommand(List<UUID> idOfRequestedValues) {
        this.idOfRequestedValues = new ArrayList<>(idOfRequestedValues);
    }

    public GetValueCommand(UUID idOfRequestedValue){
        this(Arrays.asList(idOfRequestedValue));
    }

    public List<UUID> getIdOfRequestedValues(){
        return idOfRequestedValues;
    }

    @Override
    public List<Request> accept(CommandInterpreter commandInterpreter) {
        return idOfRequestedValues.stream()
                                .map(commandInterpreter::buildGetValueCommand)
                                .collect(Collectors.toList());
    }
}
