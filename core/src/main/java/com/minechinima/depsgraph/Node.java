package com.minechinima.depsgraph;

import java.util.HashSet;
import java.util.Set;

public class Node {
    private final Set<Relation> inlinks = new HashSet<>();
    private final Set<Relation> outlinks = new HashSet<>();
}
