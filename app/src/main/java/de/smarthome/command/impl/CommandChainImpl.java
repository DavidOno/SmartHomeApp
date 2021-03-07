package de.smarthome.command.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChain;

public class CommandChainImpl implements CommandChain {

    private Iterator<Object> iterator;
    private List<Object> commands = new ArrayList<>();

    @Override
    public CommandChain add(Command command) {
        commands.add(command);
        return this;
    }

    @Override
    public CommandChain add(AsyncCommand asyncCommand) {
        commands.add(asyncCommand);
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


}
