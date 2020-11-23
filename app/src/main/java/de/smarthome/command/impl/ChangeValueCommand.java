package de.smarthome.command.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class ChangeValueCommand implements Command {

    private Map<UUID, Integer> changes;

    public ChangeValueCommand(Map<UUID, Integer> changes) {
        this.changes = changes;
    }

    public ChangeValueCommand(UUID id, Integer change){
        this(transformToMap(id, change));
    }

    private static Map<UUID, Integer> transformToMap(UUID id, Integer change) {
        Map<UUID, Integer> changeMap = new HashMap<>();
        changeMap.put(id, change);
        return changeMap;
    }

    @Override
    public List<Request> accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildChangeValueCommand();
    }
}
