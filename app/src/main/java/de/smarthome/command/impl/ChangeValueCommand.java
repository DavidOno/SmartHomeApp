package de.smarthome.command.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class ChangeValueCommand implements Command {

    private Map<String, Integer> changes;

    public ChangeValueCommand(Map<String, Integer> changes) {
        this.changes = changes;
    }

    public ChangeValueCommand(String id, Integer change){
        this(transformToMap(id, change));
    }

    private static Map<String, Integer> transformToMap(String id, Integer change) {
        Map<String, Integer> changeMap = new HashMap<>();
        changeMap.put(id, change);
        return changeMap;
    }

    @Override
    public List<Request> accept(CommandInterpreter commandInterpreter) {
        return changes.entrySet().stream()
                .map(entry -> commandInterpreter.buildChangeValueCommand(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
