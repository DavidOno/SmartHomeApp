package de.smarthome.command.impl;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChainable;
import de.smarthome.command.ResponseReactor;
import de.smarthome.command.SingleReactorCommandChain;

/**
 * This class implements the {@link SingleReactorCommandChain}
 */
public class SingleReactorCommandChainImpl implements SingleReactorCommandChain {

    private Iterator<CommandChainable> iterator;
    private final List<CommandChainable> commands = new ArrayList<>();
    private final ResponseReactor reactor;

    public SingleReactorCommandChainImpl(ResponseReactor reactor){
        this.reactor = reactor;
    }

    /**
     * @inheritDoc
     */
    @Override
    public SingleReactorCommandChain add(Command command) {
        commands.add(command);
        return this;
    }

    /**
     * @inheritDoc
     */
    @Override
    public SingleReactorCommandChain add(AsyncCommand command) {
        commands.add(command);
        return this;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean hasNext(){
        if(iterator == null){
            iterator = commands.iterator();
        }
        return iterator.hasNext();
    }

    /**
     * @inheritDoc
     */
    @Override
    public CommandChainable getNext() {
       return iterator.next();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void putResult(ResponseEntity responseEntity) {
        reactor.react(responseEntity);
    }


}
