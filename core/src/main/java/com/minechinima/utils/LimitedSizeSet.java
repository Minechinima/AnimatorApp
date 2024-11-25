package com.minechinima.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LimitedSizeSet<T> implements Set<T> {
    private final Set<T> backedSet;
    private final int maxSize;

    public LimitedSizeSet(int maxSize, Set<T> originalSet) {
        if (originalSet.size() > maxSize) {
            throw new IllegalArgumentException("The backed set size is bigger than the maximum size");
        }
        this.backedSet = originalSet;
        this.maxSize = maxSize;
    }

    /**
     * Check if the size of the backed set is still valid.
     * This is needed because we wrap a set to allow decorating different Set implementations.
     */
    private void checkSizeState() {
        if (this.backedSet.size() > this.maxSize) {
            throw new IllegalStateException("The backed set size is bigger than the maximum size");
        }
    }

    @Override
    public int size() {
        this.checkSizeState();
        return this.backedSet.size();
    }

    @Override
    public boolean isEmpty() {
        this.checkSizeState();
        return this.backedSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        this.checkSizeState();
        return this.backedSet.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        this.checkSizeState();
        return this.backedSet.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        this.checkSizeState();
        return this.backedSet.spliterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        this.checkSizeState();
        return this.backedSet.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        this.checkSizeState();
        return this.backedSet.toArray(a);
    }

    @Override
    public boolean add(T t) {
        this.checkSizeState();
        return this.backedSet.size() < this.maxSize && this.backedSet.add(t);
    }

    @Override
    public boolean remove(Object o) {
        this.checkSizeState();
        return this.backedSet.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        this.checkSizeState();
        return this.backedSet.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        this.checkSizeState();
        int countToAdd = 0;
        for (T toAdd : c) {
            if (!this.backedSet.contains(toAdd)) {
                countToAdd++;
            }
        }
        boolean success = this.backedSet.size() + countToAdd <= this.maxSize && this.backedSet.addAll(c);
        this.checkSizeState();
        return success;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        this.checkSizeState();
        return this.backedSet.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        this.checkSizeState();
        return this.backedSet.retainAll(c);
    }

    @Override
    public void clear() {
        this.backedSet.clear();
    }
}
