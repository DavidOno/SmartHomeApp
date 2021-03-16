package de.smarthome.command.impl;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChain;

public class MultiReactorCommandChainImpl implements MultiReactorCommandChain {

    private Iterator<Object> iterator;
    private List<Object> commands = new ArrayList<>();
    private List<ResponseReactor> reactors = new ArrayList<>();
    private int resultCounter = 0;

    @Override
    public CommandChain add(Command command, ResponseReactor reactor) {
        commands.add(command);
        reactors.add(reactor);
        return this;
    }

    @Override
    public CommandChain add(AsyncCommand command, ResponseReactor reactor) {
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
    public Object getNext() {
        return iterator.next();
    }

    @Override
    public void putResult(ResponseEntity responseEntity) {
        reactors.get(resultCounter).react(responseEntity);
        resultCounter++;
    }
}
