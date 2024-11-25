package com.minechinima.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class LimitedSizeSetTest {
    @Test
    public void addNotAllowed() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(0, new HashSet<>());

        Assertions.assertFalse(set.add("a"));
        testEmptyState(set);
    }

    @Test
    public void constructorWithTooMany() {
        Assertions.assertThrows(Exception.class, () -> new LimitedSizeSet<>(1, Set.of("a", "b", "c")));
    }

    @Test
    public void isEmpty() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(1, new HashSet<>());

        Assertions.assertTrue(set.isEmpty());
        Assertions.assertTrue(set.add("a"));
        Assertions.assertFalse(set.isEmpty());
    }

    @Test
    public void isEmptyPrefilledBackedSet() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(1, Set.of("a"));

        Assertions.assertFalse(set.isEmpty());
    }

    @Test
    public void size() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(1, new HashSet<>());

        Assertions.assertEquals(0, set.size());
        Assertions.assertTrue(set.add("a"));
        Assertions.assertEquals(1, set.size());
    }

    @Test
    public void sizePrefilledBackedSet() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(1, Set.of("a"));

        Assertions.assertEquals(1, set.size());
    }

    @Test
    public void add() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(2, new HashSet<>());

        Assertions.assertTrue(set.add("a"));
        Assertions.assertTrue(set.add("b"));

        testSetContainsAllState(set, Set.of("a", "b"));
    }

    @Test
    public void addTooMany() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(1, new HashSet<>());

        Assertions.assertTrue(set.add("a"));
        Assertions.assertFalse(set.add("b"));
        Assertions.assertFalse(set.add("c"));

        testSetContainsAllState(set, Set.of("a"));
    }

    @Test
    public void addExisting() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(2, new HashSet<>());

        Assertions.assertTrue(set.add("a"));
        Assertions.assertTrue(set.add("b"));
        Assertions.assertFalse(set.add("a"));
        Assertions.assertFalse(set.add("a"));

        testSetContainsAllState(set, Set.of("a", "b"));
    }

    @Test
    public void addAllTrue() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(2, new HashSet<>());

        Assertions.assertTrue(set.addAll(Set.of("a", "b")));
        testSetContainsAllState(set, Set.of("a", "b"));
    }

    @Test
    public void addAllTrueWithExisting() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(3, new HashSet<>());

        Assertions.assertTrue(set.add("a"));
        Assertions.assertTrue(set.add("b"));
        /* add elements that already exist */
        Assertions.assertTrue(set.addAll(Set.of("a", "b", "c")));
        testSetContainsAllState(set, Set.of("a", "b", "c"));
    }

    @Test
    public void addAllFalseTooManyWithExisting() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(3, new HashSet<>());

        Assertions.assertTrue(set.add("a"));
        Assertions.assertTrue(set.add("b"));
        /* add elements that already exist */
        Assertions.assertFalse(set.addAll(Set.of("a", "b", "c", "d")));
        testSetContainsAllState(set, Set.of("a", "b"));
    }

    @Test
    public void addAllFalseTooMany() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(2, new HashSet<>());

        Assertions.assertTrue(set.add("a"));
        Assertions.assertFalse(set.addAll(Set.of("b", "c", "d")));
        testSetContainsAllState(set, Set.of("a"));
    }

    @Test
    public void remove() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(2, new HashSet<>());

        Assertions.assertTrue(set.add("a"));
        Assertions.assertTrue(set.add("b"));
        Assertions.assertTrue(set.remove("a"));
        Assertions.assertTrue(set.remove("b"));

        Assertions.assertFalse(set.remove("c"));
        Assertions.assertFalse(set.remove("a"));

        testEmptyState(set);
    }

    @Test
    public void removeAllTrue() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(2, new HashSet<>());

        Assertions.assertTrue(set.add("a"));
        Assertions.assertTrue(set.add("b"));
        /* test removeAll*/
        Assertions.assertTrue(set.removeAll(Set.of("a", "b", "c")));

        testEmptyState(set);
    }

    @Test
    public void removeAllFalse() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(2, new HashSet<>());
        Assertions.assertFalse(set.removeAll(Set.of("a", "b", "c")));
        testEmptyState(set);
    }

    @Test
    public void retainAll() {
        LimitedSizeSet<String> set = new LimitedSizeSet<>(2, new HashSet<>());
        Set<String> retained = new HashSet<>();

        Assertions.assertTrue(set.add("a"));
        Assertions.assertTrue(set.add("b"));

        Assertions.assertTrue(set.retainAll(retained));

        testEmptyState(set);
        retained.containsAll(Set.of("a", "b"));
    }

    @Test
    public void containsAll() {
        LimitedSizeSet<String> test2 = new LimitedSizeSet<>(2, new HashSet<>());

        Assertions.assertTrue(test2.addAll(Set.of("a", "b")));
        Assertions.assertTrue(test2.containsAll(Set.of("a", "b")));
    }

    @Test
    public void testStateExceptionsForBackedSetSize() {
        Set<Object> set = new HashSet<>();
        LimitedSizeSet<Object> test0 = new LimitedSizeSet<>(0, set);

        Assertions.assertTrue(set.add("a"));

        Assertions.assertThrows(Exception.class, () -> test0.add("a"));
        Assertions.assertThrows(Exception.class, () -> test0.addAll(Set.of()));
        Assertions.assertThrows(Exception.class, () -> test0.retainAll(Set.of()));
        Assertions.assertThrows(Exception.class, () -> test0.iterator());
        Assertions.assertThrows(Exception.class, () -> test0.size());
        Assertions.assertThrows(Exception.class, () -> test0.spliterator());
        Assertions.assertThrows(Exception.class, () -> test0.contains("a"));
        Assertions.assertThrows(Exception.class, () -> test0.containsAll(Set.of()));
        Assertions.assertThrows(Exception.class, () -> test0.isEmpty());
        Assertions.assertThrows(Exception.class, () -> test0.toArray());
        Assertions.assertThrows(Exception.class, () -> test0.toArray(new Object[2]));
        Assertions.assertThrows(Exception.class, () -> test0.remove("a"));
        Assertions.assertThrows(Exception.class, () -> test0.removeAll(Set.of()));
    }

    private static void testEmptyState(LimitedSizeSet set) {
        Assertions.assertEquals(0, set.size());
        Assertions.assertTrue(set.isEmpty());
        Assertions.assertThrowsExactly(NoSuchElementException.class, () -> set.iterator().next());
        Assertions.assertEquals(0, set.toArray().length);
        Assertions.assertTrue(set.stream().findAny().isEmpty());
        Assertions.assertEquals(0, set.stream().spliterator().estimateSize());
    }

    private static void testSetContainsAllState(LimitedSizeSet set, Set contains) {
        /* size */
        Assertions.assertEquals(contains.size(), set.size());
        /* isEmpty */
        Assertions.assertFalse(set.isEmpty());

        /* test contains, containsAll methods */
        Assertions.assertTrue(set.containsAll(contains));
        for (Object testContainsSingle : contains) {
            Assertions.assertTrue(set.contains(testContainsSingle));
        }

        /* test iterator functionality and contains via iterator */
        Iterator it = set.iterator();
        int i = 0;
        while (i < contains.size()) {
            Assertions.assertTrue(contains.contains(it.next()));
            i++;
        }

        Assertions.assertEquals(contains.size(), set.toArray().length);
        Assertions.assertFalse(set.stream().findAny().isEmpty());
        Assertions.assertEquals(contains.size(), set.stream().spliterator().estimateSize());
    }
}
