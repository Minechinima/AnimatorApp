package com.minechinima.controller;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

public class ControllerChainTest {
    @Test
    public void testIteratorFlattenNestedChains() {

        IControllerCommand[] cmds = {new DummyCommand(), new DummyCommand(),
                new DummyCommand(), new DummyCommand(), new DummyCommand(), new DummyCommand()};


        ControllerChain chain = new ControllerChain(cmds[0])
                .next(cmds[1])
                .next(new ControllerChain(cmds[2]).next(new ControllerChain(cmds[3]).next(cmds[4])))
                .next(cmds[5]);

        int i = 0;
        for (IControllerCommand cmd : chain) {
            Assertions.assertEquals(cmds[i], cmd);
            i++;
        }
    }

    @Test
    public void testIteratorWithNulls() {

        IControllerCommand[] cmds = {new DummyCommand(), new DummyCommand(),
                new DummyCommand(), null, new DummyCommand(), new DummyCommand()};


        ControllerChain chain = new ControllerChain(cmds[0])
                .next(cmds[1])
                .next(new ControllerChain(cmds[2]).next(new ControllerChain(cmds[3]).next(cmds[4])))
                .next(cmds[5]);

        int i = 0;
        for (IControllerCommand cmd : chain) {
            if (cmds[i] == null) i++;
            Assertions.assertEquals(cmds[i], cmd);
            i++;
        }
    }

    @Test
    public void testIteratorFlatCreatedChain() {
        IControllerCommand[] cmds = {new DummyCommand(), new DummyCommand(),
                new DummyCommand(), new DummyCommand(), new DummyCommand(), new DummyCommand()};


        ControllerChain chain = new ControllerChain();
        chain.next(cmds[0]);
        chain.next(new ControllerChain().next(cmds[1]).next(cmds[2]));
        chain.next(cmds[3]);

        Iterator<IControllerCommand> commandIterator = chain.iterator();
        int i = 0;
        while (commandIterator.hasNext()) {
            IControllerCommand cmd = commandIterator.next();
            Assertions.assertEquals(cmds[i], cmd);
            i++;
        }
    }

    public class DummyCommand extends IControllerCommand {

        @Override
        protected void execute() throws UserException {

        }

        @Override
        public List<BaseStructure> getAffectedNodes() {
            return List.of();
        }
    }
}
