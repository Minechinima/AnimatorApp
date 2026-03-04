package com.minechinima.model.base;

import com.minechinima.data.IDataSerializable;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.api.IData;
import com.minechinima.model.api.IDataTree;
import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.events.types.*;
import com.minechinima.model.logging.DataOperationBus;
import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.exceptions.ContractViolatedException;
import com.minechinima.model.memory.MemoryLeakError;
import com.minechinima.model.utils.*;
import com.minechinima.utils.Pair;
import com.minechinima.model.logging.datainterfaces.IDMutable;
import com.minechinima.model.events.types.changes.IdentifierChangeEvent;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The base for all nodes in the data tree.
 * Nodes that are not subclasses of {@link BaseValueHierarchy} are considered leafs of the tree.
 * <br><br>
 * Scope of this and all subclasses:
 * This class only contains core methods and attributes relevant for the tree state.
 * <br><br>
 * A lot of methods are final in this class. This has important safety reasons and should never be changed!
 */
public abstract class BaseStructure implements IDataSerializable<BaseType<?>>, IData {
    /**
     * The String identifier used for building paths to retrieve nodes.
     * <br><br>
     *
     * @implNote This is private as it can only be changed by methods that are final to ensure
     * that the path algorithms and uniqueness cannot be overridden!
     */
    @NotNull
    private String id;
    /**
     * A UUID to uniquely identify nodes where {@link #id} might change during runtime.<br>
     * Is never null if this implements {@link IDMutable}.<br>
     * Is always null if this does not implement {@link IDMutable}.<br>
     * <br><br>
     *
     * @implNote The uuid is declared here and final to enforce that it cannot change at runtime.
     * This also forces subclasses to set the UUID in the constructor.
     * This further allows enforcing the {@link IDMutable} contract here.
     */
    @Nullable
    protected final UUID uuid;
    @Nullable
    private BaseValueHierarchy<? extends BaseStructure> parent;
    /**
     * This set contains all pointers, that are part of the same tree, that point to this node.
     * <br><br>
     * Only using ValuePointer allows better tracking pointers and relations inside the database.
     * In theory, this could allow a feature to automatically delete central data blocks in the tree
     * when no one uses them anymore.
     * <br><br>
     * The {@link com.minechinima.model.memory.ReferenceManager} is for internal runtime references,
     * which is different to this kind of link.
     */
    private final Set<ValuePointer<?>> pointersToThis = Collections.newSetFromMap(new IdentityHashMap<>());


    private final DataOperationBusImpl logger = new DataOperationBusImpl();
    /**
     * The bus for dispatching events that happen on this node.
     */
    private final EventBusImpl bus = new EventBusImpl(this);

    /**
     * @implNote give only access to package so {@link DataTreeManager} can access it.
     * Important that this is not exposed to UI. Access to the entire database should be limited by us.
     */
    private DataTreeManager dataTreeManager;

    /**
     * @param id
     * @throws ContractViolatedException if {@link IDMutable} contract is violated
     *                                   and {@link IDMutable#getUUID()} returns a different uuid than this {@link #uuid}
     */
    public BaseStructure(@NotNull String id) {
        if (this instanceof IDMutable mutable) {
            this.uuid = UUID.randomUUID();
            if (!mutable.getUUID().equals(this.uuid)) {
                throw new ContractViolatedException("IDMutable.getUUID() is not equal to this.uuid!");
            }
        } else {
            this.uuid = null;
        }

        this.id = Objects.requireNonNull(id);
    }

    /**
     * @param id
     * @param uuid if this implements {@link IDMutable} specify a custom uuid.
     * @throws ContractViolatedException if {@link IDMutable} contract is violated
     *                                   and {@link IDMutable#getUUID()} returns a different uuid than this {@link #uuid} or if the specified uuid parameter
     *                                   is null.
     * @implNote A constructor where uuid is exposed should be kept protected.
     * All subclasses should define this constructor to allow {@link IDMutable} subclasses.
     */
    protected BaseStructure(@NotNull String id, @NotNull UUID uuid) {
        if (this instanceof IDMutable && uuid == null) {
            throw new ContractViolatedException("The given uuid is null while this implements IDMutable.");
        }

        this.id = Objects.requireNonNull(id);
        this.uuid = this instanceof IDMutable ? uuid : null;

        if (this instanceof IDMutable mutable && !mutable.getUUID().equals(uuid)) {
            throw new ContractViolatedException("The provided uuid does not match the return value of IDMutable.getUUID()");
        }
    }

    @NoExceptions
    @Contract(pure = true)
    public final String getIdentifier() {
        return this.id;
    }

    @Contract(value = " -> new", pure = true)
    @NoExceptions
    public final ID getID() {
        return new ID(this.id, this.uuid);
    }

    @Contract(pure = true)
    public final DataOperationBus getDataOperationBus() {
        return this.logger;
    }

    @Contract(pure = true)
    public final EventBus getEventBus() {
        return this.bus;
    }

    /**
     * @implNote package-private to ensure only the DataTreeManager can set it,
     * as it is a core part of the tree state and should not be changed by application.
     */
    void setDataTreeManager(@Nullable DataTreeManager dataTreeManager) {
        if (dataTreeManager == null && this.getRoot() != dataTreeManager.getRoot()) {
            throw new IllegalArgumentException("Datatree manager root is different than the root node of this data.");
        }
        this.dataTreeManager = dataTreeManager;
    }

    /**
     * This method sets the parent.
     * When necessary, it resolves path collisions and changes the ID of this if possible.
     * <br><br>
     * <b>If the parent is set to null the node is automatically cleared from memory, meaning pointers are cleared!</b>
     * <br><br>
     * <b>The only possible exceptions that can come from this is {@link SetParentNotAllowed}, no RuntimeExceptions.</b>
     * <br><br>
     * Possible events triggered:
     * <ul>
     *     <li>{@link RemoveFromTreeEvent}</li>
     *     <li>{@link ParentChangeEvent}</li>
     *     <li>{@link AddToTreeEvent}</li>
     * </ul>
     *
     * @param parent can be null if the element, e.g. when it is supposed to be removed from the tree.
     * @throws SetParentNotAllowed if setting the parent is not allowed due to conflicts or other exceptions.
     *                             This will leave this element and the children elements unchanged.
     *                             Setting the parent is not allowed in the following cases:
     *                             <ul>
     *                                 <li>There is a loop in the parents i.e. some parent is reference equal to this.</li>
     *                                 <li>There is a path collision with this element and it cannot be resolved as this is not IDMutable.</li>
     *                                 <li>This element has distant children that now have path collisions
     *                                 with the distant children of the new ID uniqueness context.</li>
     *                                 <li>If this element doesn't adhere to the parent's path format</li>
     *                                 <li>If this element is still a child of another parent.
     *                                 This should enforce that BaseStructures cannot accidentally
     *                                 become children of multiple nodes.</li>
     *                                 <li>If this element is not a child of the new parent.
     *                                 This enforces the child and parent linkage and ensures that
     *                                 this method can simulate a tree state where the parent-child relationship changed
     *                                 to check if it is allowed. In the try-catch-block you should remove the child from the
     *                                 children again if this operation is not supported.</li>
     *                                 <li>If there are wonky illegal states that shouldn't be possible.</li>
     *                                 <li>Implementation of {@link #onParentChangeOfThis}</li>
     *                                 <li>If this is IDMutable and the UUID of this is not unique anymore with the neighbors.</li>
     *                                 <li>If something went wrong during {@link #afterRemoveFromTree()}</li>
     *                             </ul>
     */
    @Contract(pure = false)
    public final void setParent(@Nullable final BaseValueHierarchy<?> parent) throws SetParentNotAllowed {
        if (this.parent == parent) return;
        /*
         * declare ALL values of the tree state that might be changed during setParent here at the top
         * to safely revert them if ANY error happens.
         */
        final BaseValueHierarchy<?> originalParent = this.parent;
        final String originalIdentifier = this.id;

        BaseValueHierarchy otherRoot = parent;
        /* check for loop in parents to avoid recursion and stackoverflow */
        for (BaseValueHierarchy<? extends BaseStructure> testParent = parent; testParent != null;
             testParent = ((BaseStructure) testParent).parent) {
            if (testParent == this) {
                throw new SetParentNotAllowed("Detected loop in parent tree. Can't set parent as it results in recursion.");
            }
            otherRoot = testParent;
        }

        final boolean isInSameTree = parent != null && this.getRoot() == otherRoot;

        /*
         * Everything here is in a try-catch statement to ensure
         * that when ANY error happens, we can revert the parent and other changed states,
         * so the tree state stays untouched if setParent is not allowed.
         * Any error from this method must be a SetParentNotAllowed so the caller is forced to correct the tree state
         * e.g. children collections
         */
        try {
            final boolean isAddedToTree = originalParent == null || (parent != null && !isInSameTree);
            final boolean isRemovedFromTree = originalParent != null && (parent == null || !isInSameTree);
            /* parent change is in same tree */
            final boolean parentChange = !isAddedToTree && !isRemovedFromTree && this.parent != parent;

            /* Call this here to catch exceptions caused from getChildren() implementations.
             * Check if the tree state is correct.
             * This node must be a child of the new parent and be removed as child of the old parent.
             */
            if (this.parent != null && this.parent.getChildren().contains(this)) {
                throw new SetParentNotAllowed("Setting the parent of a BaseStructure to another element," +
                        "while it is still contained in the children of the previous parent, is prohibited to ensure tree integrity.");
            }

            if (parent != null && !parent.getChildren().contains(this)) {
                throw new SetParentNotAllowed("The new parent needs to have this in its children so the setParent() method can fully simulate" +
                        "a new tree state and check if setting the parent is allowed.");
            }

            /*
             * set parent here to fully simulate a changed tree structure so all methods of the tree work correctly.
             */
            this.parent = parent;
            /* call this here so BaseStructure implementation can also prohibit changing parent to null */
            this.onParentChangeOfThis(originalParent);
            if (this instanceof BaseValueHierarchy<?> hierarchy) {
                this.onParentChangeIterateChildren(hierarchy, originalParent);
            }

            /* The safety checks performed here are only relevant when the node is inserted into the tree, not removed. */
            if (parent != null) {
                /* test if id is allowed with the new parent e.g. path formatting etc. */
                this.throwExceptionForInvalidIDState(this.id);

                BaseValueHierarchy<?> uniquenessContext = this.parent.getPathCollisionContext();
                /*
                 * since we ensured at the beginning that the tree state is correct,
                 * this list must also include children of this element, as the children of this are now part of the tree
                 */
                List<BaseStructure> neighbors = uniquenessContext.getChildrenForPathCollisions();
                /* if we are part of the neighbors,
                 * we are part of children relevant for path collisions,
                 * meaning this node must be tested for collisions too
                 */
                boolean testThisNodeForCollisions = neighbors.contains(this);
                /* test path collisions with this node separately - see reasoning below */
                neighbors.remove(this);
                /* throws the IllegalStateException if there is an invalid path format */
                ValuePath path = this.getRelativePath(uniquenessContext);
                if (path == null) {
                    throw new IllegalStateException("Could not build a path from this element to the uniqueness context.");
                }

                /* safety check in case a UUID got serialised / duplicated incorrectly */
                if (this instanceof IDMutable thisMutable) {
                    for (BaseStructure neighbor : neighbors) {
                        if (neighbor instanceof IDMutable mutable && mutable.getUUID().equals(thisMutable.getUUID())) {
                            throw new SetParentNotAllowed("UUID is not unique with the neighbors.");
                        }
                    }
                }

                /*
                 * First try to resolve the path collisions of this.
                 * Then later check if there are still path collisions,
                 * which would fail this setParent operation and revert the changed states.
                 */
                if (testThisNodeForCollisions) {
                    String newIdentifier = PathUtils.resolvePathCollisions(this.id, this);

                    /* try to change this id if there is a collision */
                    if (!originalIdentifier.equals(newIdentifier)) {
                        if (!(this instanceof IDMutable)) {
                            throw new SetParentNotAllowed("Can't change the id to resolve a path collision of a" +
                                    " BaseStructure that does not implement IDMutable");
                        }

                        /*
                         * change it directly here
                         * don't call _setID: triggering an ID change event makes no sense,
                         * because this is the tree state we have to start off -> this ID change operation is
                         * not changing the tree state, it is creating it.
                         * also _setID would resolve Path collisions again, unnecessary operations.
                         */
                        this.id = newIdentifier;
                    }
                }

                /*
                 * Go through all neighbors that are created when setting the parent and check if
                 * any path collisions happen. Do this after trying to resolve collisions/correcting this id,
                 * to see if there are still collisions. If there are still collisions, we must fail as
                 * we cannot change other tree states to make this setParent happen. That is too unpredictable.
                 */
                for (BaseStructure child : neighbors) {
                    String newIdentifier = PathUtils.resolvePathCollisions(child.id, child);
                    if (!child.id.equals(newIdentifier)) {
                        throw new SetParentNotAllowed("Cannot set parent as children will have path collisions.");
                    }
                }

                /* changing the root means we added this node to a new tree */
                if (isAddedToTree) {
                    this.triggerEvent(new AddToTreeEvent(this));
                }
            }

            /* if this point is reached there was no exception and the operation is successful */
            if (parentChange) {
                //TODO currently deactivated as it has no use case and unecessary performacne waste.
                //this.triggerEvent(new ParentChangeEvent(this, originalParent, this.parent));
            }

            /*
             * Trigger RemoveFromTree Event at the end when it is ensured that the operation is successful.
             * Automatically call the clear references method in setParent when the parent is changed to either null or a different tree.
             * This is very important so a node cannot be accidentally removed from tree without cleaning up
             * and causing memory leaks.
             * The tree is a closed memory system: wanting to remove from the tree is synonymous with deleting it from memory.
             */
            if (isRemovedFromTree) {
                this.parent = originalParent;
                this.afterRemoveFromTree();
                this.parent = parent;
            }
        } catch (SetParentNotAllowed e) {
            this.parent = originalParent;
            this.id = originalIdentifier;
            throw e;
        } catch (Exception e) {
            this.parent = originalParent;
            this.id = originalIdentifier;
            /*
             * any exception happening here must be rethrown as SetParentNotAllowed,
             * so the caller knows exactly what is happening and can act upon it.
             */
            throw new SetParentNotAllowed(e);
        }
    }

    /**
     * Allow a subclass to define whether the given parent is structurally allowed or would
     * create an illegal structure endangering data integrity.
     * <br><br>
     * <b>This method is called after {@link #parent} has changed
     * and the check for the tree state with parent-child linkage is correct. This means you can use all methods that rely on a finished tree state.</b>
     *
     * @throws SetParentNotAllowed if setting the parent to the given argument is illegal and endangers structural integrity.
     *                             This will be called in {@link #setParent(BaseValueHierarchy)},
     *                             to allow subclasses to indirectly influence the success of that operation without overriding it.
     * @throws RuntimeException    this method is allowed to throw any runtime exception, as it will be handled in {@link #setParent(BaseValueHierarchy)}
     * @implNote This is package-private to ensure ONLY core data modules can override this. It is a core method needed for
     * the structural safety of the tree. Opening access to application data inheriting these base classes makes no sense
     * and could result in overusing/abusing it. There are other methods available for application data to ensure valid data.
     * This here is for the base data tree integrity. If your app data thinks it could create something that creates invalid
     * tree integrity, you are wrong, that would simply be invalid application data you need to validate before setting.
     */
    void onParentChangeOfThis(@Nullable final BaseValueHierarchy<?> oldParent) throws SetParentNotAllowed {
    }

    //TODO this might be performance overhead and currently only needed for ValuePointer because of relative pointer system (maybe remove relative pointer)
    private final void onParentChangeIterateChildren(@NotNull final BaseValueHierarchy<?> affectedNode,
                                                     @Nullable final BaseValueHierarchy<?> oldParent) throws SetParentNotAllowed {
        if (!(this instanceof BaseValueHierarchy<?> thisHierarchy)) return;
        for (BaseStructure child : thisHierarchy.getChildren()) {
            child.onParentChangeInParents(affectedNode, oldParent);
            if (child instanceof BaseValueHierarchy<?>) {
                child.onParentChangeIterateChildren(affectedNode, oldParent);
            }
        }
    }

    /**
     * This method is called on every child and distant child of a node where the parent changed.
     * <br><br>
     * This method is called in {@link #setParent(BaseValueHierarchy)} after parent is changed.
     * Throwing a {@link SetParentNotAllowed} exception will prevent the parent from changing and revert the tree state.
     * @param oldParent the previous parent.
     * @param affectedNode is the node where the parent changed.
     * @throws SetParentNotAllowed
     * @implNote package-private, see {@link #onParentChangeOfThis(BaseValueHierarchy)}
     */
    void onParentChangeInParents(@NotNull final BaseValueHierarchy<?> affectedNode,
                                 @Nullable final BaseValueHierarchy<?> oldParent) throws SetParentNotAllowed {
    }

    /* +------------------------------------+
     * | Parent/tree related getter methods |
     * +------------------------------------+ */

    @Nullable
    @Contract(pure = true)
    public final BaseValueHierarchy<?> getParent() {
        return this.parent;
    }

    /**
     * Goes through the parents until no parent is available anymore. This parent is considered the root.
     *
     * @return the root from this node, or this node if there is no parent anymore.
     *         Returns null if this is not a {@link BaseValueHierarchy}
     */
    @NoExceptions
    @Nullable
    @Contract(pure = true)
    public final BaseValueHierarchy<?> getRoot() {
        /* optimization - no need to traverse tree if datatreemanager is set */
        if (this.dataTreeManager != null) return this.dataTreeManager.getRoot();

        if (this.parent != null) return this.parent.getRoot();

        /* cast should be safe here, going to the top of the parents must be a BaseValueHierarchy */
        return this instanceof BaseValueHierarchy<?> h ? h : null;
    }

    @Override
    public boolean isChildOf(IDataTree parent) {
        if (!(parent instanceof BaseValueHierarchy hierarchy)) return false;
        return this.isChildOf(hierarchy);
    }

    /**
     * @param parent
     * @return true if this node is a distant child of the given parent
     * @implNote simply goes through the parents recursively, not much performance cost.
     */
    @NoExceptions
    @Contract(value = "null -> false", pure = true)
    public final boolean isChildOf(BaseValueHierarchy<? extends BaseStructure> parent) {
        if (parent == null) return false;
        if (this.parent != null && parent != this.parent) {
            return this.parent.isChildOf(parent);
        }

        return this.parent == parent;
    }

    /**
     * Goes through the parents and finds the first parent where the filter is true.
     *
     * @param filter
     * @return the first parent that satisfies the predicate or null if none was found or if there is no parent.
     */
    @Nullable
    @Contract(pure = true)
    public final BaseValueHierarchy<?> findFirstParent(final Predicate<BaseValueHierarchy<?>> filter) {
        if (this.parent == null) return null;
        if (filter.test(this.parent)) return this.parent;
        else return this.parent.findFirstParent(filter);
    }

    /**
     * Goes through the parents until the first parent is found where the filter is true.
     * While going through the parents it collects them in a list.
     *
     * @param filter
     * @return a list of all parents until the first parent is found that matches the filter.
     * The last element of the list is the parent that matches the filter.
     * The first element is the parent of this node.
     * <b>The list size is never 0.</b>
     * <br><br>
     * null if no parent was found matching the predicate.
     */
    @Nullable
    @Contract(pure = true)
    public final List<BaseValueHierarchy<?>> findFirstParentTrace(Predicate<BaseValueHierarchy<?>> filter) {
        if (this.parent == null) return null;

        List<BaseValueHierarchy<? extends BaseStructure>> parents = new ArrayList<>();
        BaseValueHierarchy<?> parentTest = this.parent;
        while (parentTest != null) {
            parents.add(parentTest);
            if (filter.test(parentTest)) {
                return parents;
            }

            parentTest = parentTest.getParent();
        }

        return null;
    }

    /**
     * Check if two nodes are part of the same tree.
     *
     * @param other
     * @return true if this node and the other node are in the same tree or are reference equal
     */
    @NoExceptions
    @Contract(pure = true)
    public final boolean isInSameTree(@NotNull BaseStructure other) {
        if (this == other) return true;
        /*
         * Both are to be considered as "root".
         * If the are different objects, they cannot be in the same tree
         */
        if (this.parent == null && other.parent == null) return false;

        return this.getRoot() == other.getRoot();
    }

    /* +-----------------------------+
     * | path related getter methods |
     * +-----------------------------+ */

    /**
     * @return the full path including the root.
     * @implNote includes all elements in the tree, even empty path elements.
     * //TODO test if full path with empty segments actually helps data operations. e.g. can an operation be performed on empty path
     */
    @NotNull
    @Contract(pure = true)
    public final ValuePath getFullPath() {
        return Objects.requireNonNull(this.getRelativePath(null));
    }

    /**
     * Appends all path segments together.
     *
     * @param relative
     * @return null if the relative element was not found in the parents or if it is this.
     * <br><br>
     * For relative == null returns the full path including the root element.
     * <br><br>
     * Otherwise, returns a path that begins at the relative element (exclusive) and ends at this element (inclusive).<br>
     * The relative path will have the relative's path format at the beginning, but not the id of the relative element.
     * @throws IllegalStateException
     * @throws ContractViolatedException {@link BaseValueHierarchy#getPathSegmentPrefixForChild(BaseStructure)}
     * @implNote includes all elements in the tree, even empty path elements.
     * This behavior should not change as the ValuePath has methods to clean it.
     */
    @Nullable
    @Contract(pure = true)
    public final ValuePath getRelativePath(@Nullable BaseValueHierarchy<?> relative) {
        /* no need to search for this in the parents */
        if (relative == this) return null;

        if (this.parent != null && !this.parent.isDirectChild(this)) {
            throw new IllegalStateException("Illegal tree state: this is not a child of this.parent..");
        }

        /* did not reach relative yet - go one hierarchy up */
        if (this.parent != relative && this.parent != null) {
            ValuePath path = this.parent.getRelativePath(relative);
            if (path != null) this.appendPathSegmentWithParent(path);
            return path;
        }

        /* this means we reached the root and relative was not in the tree */
        if (this.parent == null && relative != null) {
            return null;
        }

        /* this.parent == relative or relative == null and this.parent == null */
        ValuePath path = new ValuePath();
        if (this.parent != null) {
            this.appendPathSegmentWithParent(path);
        } else {
            path.append(this.getID());
        }

        return path;
    }

    /**
     * @param path
     * @throws IllegalStateException
     * @throws ContractViolatedException {@link BaseValueHierarchy#getPathSegmentPrefixForChild(BaseStructure)}
     */
    private final void appendPathSegmentWithParent(ValuePath path) {
        Pair<ID, ValuePath.IFormat> pair = this.parent.getPathSegmentPrefixForChild(this);
        ValuePath.IFormat idFormat = this.parent.getPathFormat();
        if (pair.getB() != null) {
            if (pair.getA() == null) {
                throw new IllegalStateException("This is not a child of this.parent according to getIdentifierPrefixForChild().");
            }
            idFormat = pair.getB();
            path.append(this.parent.getPathFormat(), pair.getA());
        }

        path.append(idFormat, this.getID());
    }

    /* +---------------------------+
     * | id-change related methods |
     * +---------------------------+ */

    /**
     * Sets the ID's identifier to the given argument,
     * if possible, and resolves path collisions by adjusting the given identifier.
     * <br>
     * Events triggered: <ul><li>{@link IdentifierChangeEvent}</li></ul>
     * <br>
     *
     * @param identifier
     * @throws RuntimeException Changing the identifier is not allowed in the following cases and will throw a RuntimeException:
     *                          <ul>
     *                              <li>This does not implement {@link IDMutable}</li>
     *                              <li>When this path segment relative to the parent is not empty and the identifier change makes it empty.<br>
     *                              Example: identifier is test and it is changed to "" (empty).<br>
     *                              parent["test"] -> parent[""] is allowed<br>
     *                              parent.test -> parent is not allowed.<br>
     *                              This is because such an empty path segment will be skipped by the path search algorithms and there could be
     *                              children exposed that create path collisions with other children.</li>
     *                              <li>The UUID returned by {@link IDMutable#getUUID()} is null</li>
     *                              <li>The UUID returned by {@link IDMutable#getUUID()} is different than the UUID in the current {@link #id}</li>
     *                              <li>The ID does not adhere to the path format of the parent.</li>
     *                          </ul>
     */
    @Contract(pure = false)
    protected final void _setID(final @NotNull String identifier) throws RuntimeException {
        if (identifier == null) {
            throw new IllegalArgumentException("Setting identifier to null is not allowed.");
        }

        /* good place to do a random check for uuid validity */
        if (this instanceof IDMutable mutable && (this.uuid == null || !mutable.getUUID().equals(this.uuid))) {
            throw new ContractViolatedException("IDMutable Contract violated.");
        }

        /*
         * If you could change the id from non empty to empty, it would mean every child of this
         * would need to be checked for path collisions and renamed. We cannot do this since
         * children might not implement IDMutable and changing this element shouldn't require changes to children.
         */
        if (this.parent != null) {
            ValuePath newPath = this.getRelativePath(this.parent);
            if (newPath == null) throw new IllegalStateException("Could not build a relative path to this parent.");
            newPath.replaceLastID(new ID(identifier, this.uuid));
            if (!this.getRelativePath(this.parent).toValuePath().isEmpty() && newPath.toValuePath().isEmpty()) {
                throw new IllegalArgumentException("Changing a non empty id to empty is not allowed when there is a parent.");
            }
        }

        final String originalID = this.id;

        if (this.parent == null) {
            this.throwExceptionForInvalidIDChange(this.id, identifier);
            this.mutate(() -> (this.id = identifier).equals(identifier),
                    () -> (this.id = originalID).equals(originalID),
                    () -> new IdentifierChangeEvent(this, originalID, identifier));
            return;
        }

        BaseValueHierarchy<?> uniquenessContext = this.parent.getPathCollisionContext();
        List<BaseStructure> neighbors = uniquenessContext.getChildrenForPathCollisions();
        final String resolvedIdentifier;
        if (!neighbors.isEmpty()) {
            resolvedIdentifier = PathUtils.resolvePathCollisions(identifier, this);
        } else {
            resolvedIdentifier = identifier;
        }

        this.throwExceptionForInvalidIDChange(this.id, resolvedIdentifier);
        this.mutate(() -> (this.id = resolvedIdentifier).equals(resolvedIdentifier),
                () -> (this.id = originalID).equals(originalID),
                () -> new IdentifierChangeEvent(this, originalID, this.id));
    }

    private final void throwExceptionForInvalidIDState(String id) {
        if (this instanceof IDMutable mutable) {
            if (mutable.getUUID() == null) {
                throw new ContractViolatedException("Interface contract violated of IDMutable. " +
                        "getUUID() of IDMutable cannot return null!");
            } else if (!mutable.getUUID().equals(this.uuid)) {
                throw new ContractViolatedException("IDMutable.getUUID() is not equal to this.uuid");
            }
        }

        if (this.parent != null) {
            if (this.parent.getPathFormatPrefixForChildren() != null
                    && !this.parent.getPathFormatPrefixForChildren().isValidStringForEnclosure(id)) {
                throw new IllegalArgumentException("The identifier does not adhere to the parent's prefix path format for this child.");
            } else if (this.parent.getPathFormatPrefixForChildren() == null
                    && !this.parent.getPathFormat().isValidStringForEnclosure(id)) {
                throw new IllegalArgumentException("The identifier does not adhere to the parent's path format.");
            }
        }
    }

    /**
     * @throws IllegalStateException if this does not implement IDMutable or if UUID is null.
     */
    private final void throwExceptionForInvalidIDChange(String id, String newID) {
        if (!(this instanceof IDMutable mutable)) {
            throw new IllegalStateException("You cannot change the ID of a BaseStructure that does not implement IDMutable interface.");
        }

        this.throwExceptionForInvalidIDState(newID);
    }

    /* +-----------------------+
     * | event related methods |
     * +-----------------------+ */

    /**
     * Records the change event to all the loggers registered in this node and all the parent nodes.
     *
     * @param event
     */
    private void bubbleDataOperation(ChangeEvent<?> event) {
        this.logger._logDataOperation(event.getTrigger(), event.getDataOperation());

        if (this.parent != null) {
            ((BaseStructure) this.parent).bubbleDataOperation(event);
        }
    }

    private final <T extends AbstractEvent> void bubbleChildEvent(T event) {
        this.onChildEvent(event);
        this.bus._dispatchEventFromChildNode(event);
        if (this.parent != null) {
            ((BaseStructure) this.parent).bubbleChildEvent(event);
        }
    }

    /**
     * Mutate data and trigger a change event. The callbacks represent template method pattern.
     * <br><br>
     * <b>Important:</b> all the logic should be split and encapsulated into the lambdas,
     * to allow mutability control.
     * @param setter perform the mutation of the internal datastructure. Return false or any exception will trigger the undo callback.
     * @param undo undo the mutation. Any exception here will crash the app to prevent corrupt data.
     *             Returnining false will also crash the app-
     * @param createChangeEvent create the change event here so we can catch potential exceptions, e.g. when the event performs
     *                          serialization.
     * @param <T>
     */
    protected final <T extends ChangeEvent<?>> void mutate(Callable<Boolean> setter,
                                                           Callable<Boolean> undo,
                                                           Supplier<T> createChangeEvent) {
        if (this.dataTreeManager != null && !this.dataTreeManager.canMutate()) {
            throw new UnsupportedOperationException("Cannot mutate data when mutation gate is closed. Likely no transaction is running.");
        }

        final T event;
        try {
            boolean wasBufferingBefore = false;
            if (this.dataTreeManager != null) {
                wasBufferingBefore = this.dataTreeManager.getEventBusManager().isBuffering();
                this.dataTreeManager.getEventBusManager().startBuffering();
            }

            /* setter might cause events, like internal events when removing a node
             * These events should be buffered since mutation might fail even because of failed event creation 
             * throw exception to trigger undo and eventbusmanager buffering.
             */
            if (!setter.call()) {
                throw new RuntimeException("Setter returned false.");
            }
            event = createChangeEvent.get();

            /* if someone else started buffering before it will handle flushing */
            if (this.dataTreeManager != null && !wasBufferingBefore) {
                this.dataTreeManager.getEventBusManager().stopBuffering();
                this.dataTreeManager.getEventBusManager().flush();
            }

            /* for change events terminate, as logging needs to always be successful. */
            if (event.getTrigger() != this) {
                throw new Error("ChangeEvent has incorrect triggering node set.");
            }
        } catch (Exception e) {
            try {
                if (!undo.call()) throw new Error("Could not recover from failed data operation, undo method returned false.");
            } catch (Exception undoE) {
                throw new Error("Could not recover from failed data operation", undoE);
            } finally {
                if (this.dataTreeManager != null) {
                    this.dataTreeManager.getEventBusManager().stopBuffering();
                    this.dataTreeManager.getEventBusManager().clearBuffer(); //TODO this would clear entire buffer, what if buffering was done before. Buffer Frames?
                }
            }

            throw new RuntimeException(e);
        }

        /*
         * perform logging outside try-catch, as internal undo will be unecessary: either operation logged (becomes undo-able),
         * or logging failed and app crashes.
         * Log changes before triggering events.
         * Events might result in new changes, so first record the source change for correct chronological order.
         */
        try {
            this.bubbleDataOperation(event);
        } catch (Exception e) {
            /*
             * If there is a problem with logging, it is a critical bug, as we cannot revert changes properly.
             * The App needs to crash, so we can quickly fix the underlying issue!
             */
            throw new Error("An exception happened during logging the change. This is a critical bug!", e);
        }

        /* trigger the event after logging and internal data change -> any exception here does not harm the data, as
         * the operation was logged. */
        this._triggerEvent(event);
    }

    /**
     * Triggers an event on this element and passes and event to the parents.
     * <br><br>
     *
     *
     * <h2>Important:</h2><br>
     * The logging happens first. If any exception happens during logging, the app is terminated with an Error.
     * Therefore, you can assume that this method will not cause an exception that prevents logging a change, as a failed logging
     * will terminate.
     *
     * @param event
     * @throws IllegalArgumentException if the trigger of the event is not equal to this.
     * @implNote has {@link NoExceptions} annotation, because in the context of logging and reverting a change: if anything
     * goes wrong during logging the change, the app will crash with an Error. Any exceptions after logging do not matter as the
     * change becomes revertable as soon as it is logged
     */
    @NoExceptions
    protected final <T extends Event> void triggerEvent(T event) {
        this._triggerEvent(event);
    }

    /**
     * Internal trigger Event method only for this class. Subclasses should either use {@link #triggerEvent(Event)} or
     * for {@link ChangeEvent} the mutate method. This is to enforce on compiler level that changes / mutation and data operations
     * can only happen there to control mutability.
     * @param event
     * @param <T>
     */
    @NoExceptions
    private final <T extends AbstractEvent> void _triggerEvent(T event) {
        if (event.getTrigger() != this) {
            /* for change events terminate, as logging needs to always be successful. */
            if (event instanceof ChangeEvent<?>) throw new Error("Cannot trigger event where the trigger is not this.");
            else throw new IllegalArgumentException("Cannot trigger event where the trigger is not this.");
        }

        //TODO for future event system:
        // events should be dispatched through a stream API for buffering and better connectivity. Buffering the entire method
        // call is wonky. Also remove internal onEvent and register this node as FIRST event receiver to the bus,
        // to streamline onEvent and participate internal onEvent in buffering.
        Runnable eventCall = () -> {
            this.onEvent(event);
            this.bus._dispatchEventOnThis(event);

            /*
             * Dieser Code wurde in der Gemeinschaft mit Marlon gesegnet (3.10.2024 23:22 Areon Pictures Discord).
             * In nomine Patris et Fili, et Spiritus Sancti. Amen.
             * Die Architektur liegt nun in Gottes Händen. ≽^•⩊•^≼ mowmow
             * FurryFlo war auch dabei.
             */
            if (this.parent != null) {
                ((BaseStructure) this.parent).bubbleChildEvent(event);
            }
        };

        if (this.dataTreeManager != null && this.dataTreeManager.getEventBusManager().isBuffering()) {
            this.dataTreeManager.getEventBusManager().bufferEventCall(eventCall);
        } else {
            eventCall.run();
        }
    }

    /**
     * For internal event handling code without the event bus overhead.
     * @param event
     */
    protected void onEvent(AbstractEvent event) { }

    /**
     * For internal event handling code without the event bus overhead.
     * @param event
     */
    protected void onChildEvent(AbstractEvent event) { }

    /**
     * Template method pattern, also enforce important things that should always happen when removing from the tree.
     * <br><br>
     * Triggers {@link RemoveFromTreeEvent}
     * <br><br>
     * Frees all pointers in this node and all children.
     */
    private final void afterRemoveFromTree() {
        /* only trigger event once on the element that this method is called */
        this.triggerEvent(new RemoveFromTreeEvent(this));
        this.afterRemoveFromTreeIteration();
    }

    private void afterRemoveFromTreeIteration() {
        for (ValuePointer<?> pointer : this.pointersToThis) {
            pointer.set(null);
            if (pointer.get() == this) {
                throw new MemoryLeakError("Could not clear pointer.");
            }
        }

        this.pointersToThis.clear();
        if (this instanceof BaseValueHierarchy<?> hierarchy) {
            hierarchy.getChildren().forEach(BaseStructure::afterRemoveFromTreeIteration);
        }
    }

    /* +-------------------------+
     * | Pointer related methods |
     * +-------------------------+*/

    /**
     * Add the pointer to a collection.
     *
     * @throws UnsupportedOperationException if the pointer type is not compatible with this class.
     * @throws IllegalArgumentException      if the pointer is not part of the same tree.
     * @implNote package-private so only ValuePointer can add itself as a pointer to a tree node.
     */
    final void addPointer(ValuePointer<?> pointer) {
        if (!pointer.getType().isAssignableFrom(this.getClass())) {
            throw new UnsupportedOperationException("Cannot add a pointer link when the type of the pointer does not match this class.");
        }

        if (!this.isInSameTree(pointer)) {
            throw new IllegalArgumentException("Tried adding a pointer to a node that is not part of the same data tree.");
        }

        if (pointer.get() != this) {
            throw new IllegalArgumentException("Cannot add a pointer to a node that does not reference the node.");
        }

        this.pointersToThis.add(pointer);
    }

    /**
     * Remove the pointer from a collection
     * <br><br>
     * A pointer needs to call this when it does not point to this anymore.
     *
     * @throws IllegalArgumentException if the pointer still references this node.
     * @implNote package-private so only ValuePointer can remove itself as a pointer to a tree node.
     */
    final void removePointer(ValuePointer<?> pointer) {
        if (pointer.get() == this) {
            throw new IllegalArgumentException("Cannot remove a pointer from a referenced node that still points to it.");
        }
        this.pointersToThis.remove(pointer);
    }


    /**
     * Implementation of EventBus for the data tree, so the only possible way
     * to dispatch an event is in a BaseStructure.
     * The dispatch methods are therefore package-private
     */
    private class EventBusImpl extends EventBus {
        public EventBusImpl(BaseStructure owner) {
            super(owner);
        }

        final void _dispatchEventOnThis(AbstractEvent event) {
            super.dispatchEventOnThis(event);
        }

        final void _dispatchEventFromChildNode(AbstractEvent event) {
            super.dispatchEventFromChildNode(event);
        }
    }

    /**
     * Hide this class here to ensure that only the node where the change was triggered can log the operation.
     */
    private class DataOperationBusImpl extends DataOperationBus {
        protected void _logDataOperation(BaseStructure context, IDataOperation<?> dataOperation) {
            super.logDataOperation(context, dataOperation);
        }
    }
}
