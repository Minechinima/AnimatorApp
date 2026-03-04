package com.minechinima.model.base;

import com.minechinima.model.api.IData;
import com.minechinima.model.api.IDataTree;
import com.minechinima.model.exceptions.ContractViolatedException;
import com.minechinima.model.exceptions.PathParsingException;
import com.minechinima.model.utils.ID;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.utils.Pair;
import com.minechinima.model.logging.datainterfaces.IDMutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Template class for all BaseStructure elements that can have children that are part of the tree structure.
 * This class mainly contains getter methods that have the logic to traverse the tree with paths.
 * These are final because they are part of the path system specification
 * and should not be overridden.
 * @param <T>
 */
public sealed abstract class BaseValueHierarchy<T extends BaseStructure> extends BaseStructure implements IDataTree permits BaseImmutableValueHierarchy, BaseMutableValueHierarchy {
    private final ValuePath.IFormat format;

    public BaseValueHierarchy(String id, ValuePath.IFormat pathFormat) {
        super(id);
        this.format = pathFormat;
    }

    protected BaseValueHierarchy(String id, UUID uuid, ValuePath.IFormat pathFormat) {
        super(id, uuid);
        this.format = pathFormat;
    }

    /**
     * Determines how the child id is appended to the path.
     */
    public final ValuePath.IFormat getPathFormat() {
        return this.format;
    }

    /**
     * <b>ONLY OVERRIDE THE TEMPLATE METHODS IF YOU CAN GUARANTEE THE PATH SEGMENTS WILL BE UNIQUE.</b>
     * <br>
     * Get a path segment for building the path for a given direct child of this BaseValueHierarchy element.
     * The path segment will be inserted directly after this node's id and {@link #format}.<br>
     * The path format from this method therefore influences the child while this node's {@link #format} is for the path segment
     * returned by this method.
     * <br><br>
     * @return a path segment for the given child. When the ID part is null, it means the child was not found.
     * @throws ContractViolatedException if {@link #getIDPrefixForChild} is not null but {@link #getPathFormatPrefixForChildren} is null,
     * violating this method contract.
     * Or if {@link #isDirectChild(BaseStructure)} is true
     * and {@link #getPathFormatPrefixForChildren()} is overridden but no id was given.
     */
    protected final Pair<ID, ValuePath.IFormat> getPathSegmentPrefixForChild(BaseStructure child) {
        ValuePath.IFormat prefix = this.getPathFormatPrefixForChildren();
        ID id = this.getIDPrefixForChild(child);
        boolean overriddenFormat = prefix != null;

        if (id != null && !overriddenFormat) {
            throw new ContractViolatedException("getPathFormatPrefixForChildren() is not overridden but getIDPrefixForChild() is.");
        } else if (id == null && overriddenFormat && this.isDirectChild(child)) {
            throw new ContractViolatedException("getPathFormatPrefixForChildren() is overridden, but getIDPrefixForChild() returns null for a direct child.");
        }

        return new Pair<>(id, prefix);
    }

    /**
     * For {@link #getPathSegmentPrefixForChild(BaseStructure)}
     * @return never null if {@link #getIDPrefixForChild(BaseStructure)} is not null.
     * @see BaseValueHierarchy#getPathSegmentPrefixForChild(BaseStructure)
     */
    @Nullable
    protected ValuePath.IFormat getPathFormatPrefixForChildren() {
        return null;
    }

    /**
     * For {@link #getPathSegmentPrefixForChild(BaseStructure)}<br>
     * If this is overridden, you also need to override {@link #getPathFormatPrefixForChildren()}.
     * @param child
     * @return null if the child is not found or if it is not overridden.
     * @see BaseValueHierarchy#getPathSegmentPrefixForChild(BaseStructure)
     */
    @Nullable
    protected ID getIDPrefixForChild(BaseStructure child) {
        return null;
    }

    /**
     * @return collection of children.
     * Never contains null elements or duplicates (reference equal).
     * The specification of how the elements are ordered is open for the subclasses to define.
     */
    public abstract Collection<T> getChildren();

    /**
     * @param uuid
     * @return true if one of the direct children is {@link IDMutable} and returns the given UUID.
     */
    public final boolean isUUIDPresent(UUID uuid) {
        for (T child : this.getChildren()) {
            if (child instanceof IDMutable mutable
                    && mutable.getUUID().equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param child
     * @return true if the specified child is a direct child of this hierarchy
     * @implNote subclasses should define this to avoid unnecessary {@link #getChildren()} calls that
     * create new list objects
     */
    public abstract boolean isDirectChild(BaseStructure child);

    @Override
    public boolean isDirectChild(IData child) {
        if (!(child instanceof BaseStructure structure)) return false;
        return this.isDirectChild(structure);
    }

    @Nullable
    public final BaseStructure getChildByValuePath(String relativePath) {
        if (relativePath.isEmpty()) return null;
        try {
            return this.getChildByValuePath(new ValuePath(relativePath));
        } catch (PathParsingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public final BaseStructure getChildByUniquePath(String uniqueRelativePath) {
        if (uniqueRelativePath.isEmpty()) return null;
        try {
            return this.getChildByUniquePath(new ValuePath(uniqueRelativePath));
        } catch (PathParsingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public final BaseStructure getChildByValuePath(ValuePath path) {
        return this.getChildByPathRecursive(path.getCleanedValuePath().getStart(), null, false);
    }

    @Nullable
    public final BaseStructure getChildByUniquePath(ValuePath path) {
        return this.getChildByPathRecursive(path.getCleanedUniquePath().getStart(), null, true);
    }

    /**
     * @param path <b>ALWAYS</b> the cleaned path without concat in the beginning.
     *             Fully cleaned as if it could be parsed from a string.
     * @param useUniquePath
     * @return
     */
    @Nullable
    private final BaseStructure getChildByPathRecursive(@Nullable ValuePath.PathSegment path,
                                                        @Nullable ValuePath.CleanPathBuilder childPathBuilder,
                                                        boolean useUniquePath) {
        if (path == null) return null;

        for (BaseStructure child : this.getChildren()) {
            ValuePath.PathSegment currentSearch = path;
            BaseValueHierarchy<?> parent = child.getParent();
            if (parent == null) throw new IllegalStateException("child does not have parent");
            ValuePath relativePath = child.getRelativePath(parent);
            if (relativePath == null) throw new IllegalStateException("relative path to parent could not be built.");

            final int prevSize;
            final ValuePath childPath;
            if (childPathBuilder == null) {
                childPathBuilder = useUniquePath ? relativePath.getUniquePathIterator() : relativePath.getValuePathIterator();
                prevSize = 0;
                while (childPathBuilder.nextNonEmpty() != null) {}
                childPath = childPathBuilder.getBuiltList();
            } else {
                prevSize = childPathBuilder.getBuiltList().getSize();
                childPath = childPathBuilder.append(relativePath);
            }

            /* empty path element - if it is a hierarchy, go down */
            if (childPath.getStart() == null && child instanceof BaseValueHierarchy<?> subHierarchy) {
                BaseStructure testChild = subHierarchy.getChildByPathRecursive(currentSearch, childPathBuilder, useUniquePath);
                if (testChild != null) {
                    return testChild;
                } else {
                    childPathBuilder.getBuiltList().reduceToSize(prevSize);
                    continue;
                }
            } else if (childPath.getStart() == null) {
                childPathBuilder.getBuiltList().reduceToSize(prevSize);
                continue;
            }

            boolean matching = false;
            for (ValuePath.PathSegment current = childPath.getStart(); current != null; current = current.getNext()) {

                /* the search path ended but the child path not, this means it must be the wrong child */
                if (currentSearch == null) {
                    break;
                }

                if (currentSearch.equals(current, useUniquePath)) {
                    /* it is matching if all segments are equal to the end of the child path */
                    matching = current.getNext() == null;
                    currentSearch = currentSearch.getNext();
                } else {
                    /* as soon as a child segment does not match abort */
                    break;
                }
            }

            if (matching) {
                /* found child if every child segment matched and no segments are left */
                if (currentSearch == null) {
                    return child;
                } else if (child instanceof BaseValueHierarchy<?> subHierarchy) {
                    return subHierarchy.getChildByPathRecursive(currentSearch, childPathBuilder, useUniquePath);
                }
            } else {
                childPathBuilder.getBuiltList().reduceToSize(prevSize);
            }
        }

        return null;
    }

    /**
     * This method goes through all children and distant children of this node and collects those where the relative path
     * to this is not empty and therefore the child is addressable.
     * @return a list of all children and distant children where the relative path to this is not empty anymore.
     *         This means tree ends where the entire path is empty will be skipped.
     *         <br>This is because the path search algorithm
     *         will also skip such empty elements and therefore path collisions cannot occur.
     *         <br>Calling {@link BaseStructure#getRelativePath(BaseValueHierarchy)} on the elements
     *         returned by this method, with this as relative, will not return an empty path.
     *         <br><br>
     *         This needs to be used to check for path collisions.
     */
    public final List<BaseStructure> getChildrenForPathCollisions() {
        return this.getChildrenForPathCollisions(this);
    }

    /**
     * Finds the element from which we need to check for path collisions.
     * @return if this element has no parent or if the relative path to this is not empty, it will return this.
     *         Otherwise, it will search for the next parent where the path is not empty anymore. This element is
     *         where we need to flatten all children with empty paths to check for path collisions.
     */
    @NotNull
    public final BaseValueHierarchy<?> getPathCollisionContext() {
        if (this.getParent() == null) return this;
        BaseValueHierarchy<?> uniquenessContext = this;

        /* if there is no parent, it means it is the root. */
        while (uniquenessContext.getParent() != null) {
            ValuePath path = this.getRelativePath(uniquenessContext.getParent());

            if (path == null) throw new IllegalStateException("Couldn't build a path from a child to its parent.");
            if (!path.toValuePath().isEmpty()) break;

            uniquenessContext = uniquenessContext.getParent();
        }

        return uniquenessContext;
    }

    private List<BaseStructure> getChildrenForPathCollisions(final BaseValueHierarchy<?> relative) {
        List<BaseStructure> children = new ArrayList<>();
        for (BaseStructure child : this.getChildren()) {
            ValuePath childPath = child.getRelativePath(relative);
            if (childPath == null) {
                throw new IllegalStateException("A distant child of a BaseValueHierarchy does not" +
                        " have the BaseValueHierarchy in its parents.");
            }

            if (childPath.toValuePath().isEmpty()) {
                if (child instanceof BaseValueHierarchy<?> hierarchy) {
                    children.addAll(hierarchy.getChildrenForPathCollisions(relative));
                }
            } else {
                children.add(child);
            }
        }

        return children;
    }
}
