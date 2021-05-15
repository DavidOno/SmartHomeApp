package de.smarthome.command.impl;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChainable;
import de.smarthome.command.MultiReactorCommandChain;
import de.smarthome.command.ResponseReactor;

public class MultiReactorCommandChainImpl implements MultiReactorCommandChain {

    private Iterator<CommandChainable> iterator;
    private final List<CommandChainable> commands = new ArrayList<>();
    private final List<ResponseReactor> reactors = new ArrayList<>();
    private int resultCounter = 0;

    @Override
    public MultiReactorCommandChain add(Command command, ResponseReactor reactor) {
        commands.add(command);
        reactors.add(reactor);
        return this;
    }

    @Override
    public MultiReactorCommandChain add(AsyncCommand command, ResponseReactor reactor) {
        commands.add(command);
        reactors.add(reactor);
        return this;
    }

    @Override
    public boolean hasNext(){
        if(iterator == null){
            iterator = commands.iterator();
        }
        return iterator.hasNext();
    }

    @Override
    public CommandChainable getNext() {
        return iterator.next();
    }

    @Override
    public void putResult(ResponseEntity responseEntity) {
        reactors.get(resultCounter).react(responseEntity);
        resultCounter++;
    }
}
