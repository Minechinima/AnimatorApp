package com.minechinima.depsgraph.tagging;

/**
 * Default node tags provided by the core dependency graph system.
 * <br><br>
 * Addons can define their own {@link INodeTag} implementations (e.g. additional enums)
 * to extend the tagging system at runtime.
 */
public enum NodeTag implements INodeTag {
    /**
     * Marks a node as needing re-evaluation because the data it depends on has changed.
     */
    NEEDS_REEVALUATION,
    REBUILD_TREE;

    @Override
    public String getName() {
        return this.name();
    }
}
