package com.minechinima.controller;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Allows to reuse other controllers in a composition of controller commands.
 */
public class ControllerChain extends IControllerCommand implements Iterable<IControllerCommand> {
    @Nullable
    private final IControllerCommand command;
    @Nullable
    private ControllerChain next;
    /**
     * Keep track of the head for the {@link ControllerManager} to execute the chain
     * from the start in case the middle/end of the chain is accidentally given as input.
     */
    @NotNull
    protected final ControllerChain head;
    /**
     * only not null for the {@link #head}.
     */
    @Nullable
    protected ControllerChain tail;

    public ControllerChain() {
        this(null);
    }

    public ControllerChain(@NotNull IControllerCommand command) {
        this.command = command;
        this.head = this;
        this.head.tail = this;
    }

    private ControllerChain(@NotNull IControllerCommand command, @NotNull ControllerChain head) {
        this.command = command;
        this.head = head;
    }

    /**
     * Add a controller command to the chain. Always adds to the end of the chain.
     * If another chain is passed here, the head of that chain will be appended, effectively flattening the chains
     * and always ensuring a consecutive order.
     * @param command
     * @return the next controller chain node to continue chaining.
     */
    public ControllerChain next(@NotNull IControllerCommand command) {
        if (command instanceof ControllerChain chain) {
            if (chain.head == this.head) throw new IllegalArgumentException("Cannot reinsert a chain node into the same chain.");
            else command = chain.head;
        }
        /* add at the end of the chain */
        if (this.next != null) return this.head.tail.next(command);
        this.next = new ControllerChain(command, this.head);
        this.head.tail = this.next;
        return this.next;
    }

    @Override
    public void execute() throws UserException {
        /* start from the head */
        if (this.head != this) this.head.execute();

        if (this.command != null) this.command.execute();
        if (this.next != null) this.next.execute();
    }

    @Override
    public List<BaseStructure> getAffectedNodes() {
        List<BaseStructure> affected = new ArrayList<>();
        ControllerChain next = this;

        while (next != null) {
            if (next.command != null) affected.addAll(next.command.getAffectedNodes());
            next = next.next;
        }

        return List.copyOf(affected);
    }

    @Override
    public @NotNull Iterator<IControllerCommand> iterator() {
        return new ControllerChainIterator(this);
    }

    /**
     * Iterator that flattens all nested controller chains so we can iterate through every single command.
     * Does not iterate through null values.
     */
    public final class ControllerChainIterator implements Iterator<IControllerCommand> {
        private final Stack<Iterator<IControllerCommand>> stack = new Stack<>();

        public ControllerChainIterator(ControllerChain start) {
            this.stack.push(flatIterator(start.head));
        }

        @Override
        public boolean hasNext() {
            while (!this.stack.isEmpty()) {
                Iterator<IControllerCommand> it = stack.peek();

                if (!it.hasNext()) {
                    this.stack.pop();
                    continue;
                }

                IControllerCommand next = it.next();

                if (next == null) {
                    continue;
                }

                if (next instanceof ControllerChain chain) {
                    this.stack.push(flatIterator(chain.head));
                    continue;
                }

                this.stack.push(Collections.singleton(next).iterator());
                return true;
            }
            return false;
        }

        @Override
        @NotNull
        public IControllerCommand next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return this.stack.pop().next();
        }

        /**
         * Creates a flat iterator over a single ControllerChain (no nesting).
         */
        private static Iterator<IControllerCommand> flatIterator(ControllerChain chain) {
            return new Iterator<>() {
                private ControllerChain current = chain;

                @Override
                public boolean hasNext() {
                    return this.current != null;
                }

                @Override
                public IControllerCommand next() {
                    if (this.current == null) {
                        throw new NoSuchElementException();
                    }
                    IControllerCommand cmd = this.current.command;
                    this.current = this.current.next;
                    return cmd;
                }
            };
        }
    }

}
