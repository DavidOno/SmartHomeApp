package de.smarthome.command.impl;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChain;
import de.smarthome.command.CommandChainable;

public class SingleReactorCommandChainImpl implements SingleReactorCommandChain {

    private Iterator<CommandChainable> iterator;
    private List<CommandChainable> commands = new ArrayList<>();
    private ResponseReactor reactor;

    public SingleReactorCommandChainImpl(ResponseReactor reactor){
        this.reactor = reactor;
    }


    @Override
    public CommandChain add(Command command) {
        commands.add(command);
        return this;
    }

    @Override
    public CommandChain add(AsyncCommand command) {
        commands.add(command);
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
        reactor.react(responseEntity);
    }


}