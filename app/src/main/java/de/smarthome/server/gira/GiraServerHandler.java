package de.smarthome.server.gira;

import org.apache.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.server.ServerHandler;

public class GiraServerHandler implements ServerHandler {

    private CommandInterpreter commandInterpreter;

    public GiraServerHandler(CommandInterpreter commandInterpreter) {
        this.commandInterpreter = commandInterpreter;
    }

    @Override
    public void sendRequest(Command command) {
        List<Request> requests = command.accept(commandInterpreter);
//        ResponseEntity<String> result = request.execute(); //TODO: Check generic String: has to be improved
        List<ResponseEntity> results = requests.stream().map(Request::execute).collect(Collectors.toList());
    }

    @Override
    public String receiveRequest() {
        return null;
    }


}
