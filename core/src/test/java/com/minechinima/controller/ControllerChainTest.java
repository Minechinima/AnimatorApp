package com.minechinima.controller;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ControllerChainTest {
    @Test
    public void testExecuteFromNodeOtherThanHead() throws UserException {
        List<Integer> executionOrder = new ArrayList<>();

        IControllerCommand cmd1 = new DummyCommand() {
            @Override
            protected void execute() {
                executionOrder.add(1);
            }
        };
        IControllerCommand cmd2 = new DummyCommand() {
            @Override
            protected void execute() {
                executionOrder.add(2);
            }
        };
        IControllerCommand cmd3 = new DummyCommand() {
            @Override
            protected void execute() {
                executionOrder.add(3);
            }
        };

        ControllerChain chain = new ControllerChain(cmd1);
        ControllerChain middle = chain.next(cmd2);
        ControllerChain end = middle.next(cmd3);

        // Execute from the middle node, should still start from head
        middle.execute();
        Assertions.assertEquals(List.of(1, 2, 3), executionOrder);

        executionOrder.clear();

        end.execute();
        Assertions.assertEquals(List.of(1, 2, 3), executionOrder);
    }

    @Test
    public void testExecuteFromNodeReturnedByNext() throws UserException {
        List<Integer> executionOrder = new ArrayList<>();

        IControllerCommand cmd1 = new DummyCommand() {
            @Override
            protected void execute() {
                executionOrder.add(1);
            }
        };
        IControllerCommand cmd2 = new DummyCommand() {
            @Override
            protected void execute() {
                executionOrder.add(2);
            }
        };
        IControllerCommand cmd3 = new DummyCommand() {
            @Override
            protected void execute() {
                executionOrder.add(3);
            }
        };

        ControllerChain chain = new ControllerChain(cmd1).next(cmd2).next(cmd3);

        // Execute from the middle node, should still start from head
        chain.execute();
        Assertions.assertEquals(List.of(1, 2, 3), executionOrder);
    }

    @Test
    public void testChainNextFromIControllerCommandAPI() throws UserException {
        List<Integer> executionOrder = new ArrayList<>();

        IControllerCommand cmd1 = new DummyCommand() {
            @Override
            protected void execute() {
                executionOrder.add(1);
            }
        };
        IControllerCommand cmd2 = new DummyCommand() {
            @Override
            protected void execute() {
                executionOrder.add(2);
            }
        };
        IControllerCommand cmd3 = new DummyCommand() {
            @Override
            protected void execute() {
                executionOrder.add(3);
            }
        };

        ControllerChain chain = cmd1.next(cmd2).next(cmd3);

        // Execute from the middle node, should still start from head
        chain.execute();
        Assertions.assertEquals(List.of(1, 2, 3), executionOrder);
    }

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
    public void reinsertinngNodeIntoItselfException() {
        ControllerChain chain = new ControllerChain(new DummyCommand());
        ControllerChain next = chain.next(new DummyCommand());
        Assertions.assertThrows(IllegalArgumentException.class, () -> next.next(next));
    }

    @Test
    public void reinsertingSameChainException() {
        ControllerChain chain = new ControllerChain(new DummyCommand());
        ControllerChain next = chain.next(new DummyCommand()).next(new DummyCommand());
        Assertions.assertThrows(IllegalArgumentException.class, () -> chain.next(next));
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
