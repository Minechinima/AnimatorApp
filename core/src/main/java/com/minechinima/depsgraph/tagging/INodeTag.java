package com.minechinima.depsgraph.tagging;

import com.minechinima.depsgraph.Node;

/**
 * Extensible interface for node tags in the dependency graph.
 * <br><br>
 * A tag is a label that can be applied to any {@link Node} in the dependency graph to mark it
 * for a specific purpose, e.g. re-evaluation when data changes.
 * <br><br>
 * The core system provides a default enum {@link NodeTag NodeTag}, but addons can implement this interface
 * with their own enums or classes to extend the tagging system at runtime.
 */
public interface INodeTag {
    /**
     * @return the name of this tag, used for debugging and identification.
     */
    String getName();
}


