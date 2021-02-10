package de.smarthome.command;

import java.util.List;
import java.util.function.Consumer;

public interface AsyncCommand {

    void accept(CommandInterpreter commandInterpreter, Consumer<List<Request>> callback);
}
