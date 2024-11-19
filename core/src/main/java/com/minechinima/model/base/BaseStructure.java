package com.minechinima.model.base;

import com.minechinima.data.IDataSerializable;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.dataoperations.DataOperationLog;
import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.ParentChangeEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.exceptions.ContractViolatedException;
import com.minechinima.model.utils.*;
import com.minechinima.utils.Pair;
import com.minechinima.model.dataoperations.datainterfaces.IDMutable;
import com.minechinima.model.events.types.changes.ChangeEvent;
import com.minechinima.model.events.types.changes.IdentifierChangeEvent;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The base for all nodes in the data tree.
 * Nodes that are not subclasses of {@link BaseValueHierarchy} are considered leafs of the tree.
 * <br><br>
 * Scope of this and all subclasses:
 * This class only contains core methods and attributes relevant for the tree state.
 * <br><br>
 * A lot of methods are final in this class. This has important safety reasons and should never be changed!
 */
public abstract class BaseStructure implements IDataSerializable<BaseType<?>> {
    /**
     * The String identifier used for building paths to retrieve nodes.
     * <br><br>
     * @implNote This is private as it can only be changed by methods that are final to ensure
     * that the path algorithms and uniqueness cannot be overridden!
     */
    private String id;
    /**
     * A UUID to uniquely identify nodes where {@link #id} might change during runtime.<br>
     * Is never null if this implements {@link IDMutable}.<br>
     * Is always null if this does not implement {@link IDMutable}.<br>
     * <br><br>
     * @implNote The uuid is declared here and final to enforce that it cannot change at runtime.
     * This also forces subclasses to set the UUID in the constructor.
     * This further allows enforcing the {@link IDMutable} contract here.
     */
    @Nullable
    protected final UUID uuid;
    @Nullable
    private BaseValueHierarchy<? extends BaseStructure> parent;
    /**
     * The bus for dispatching events that happen on this node.
     */
    private final EventBusImpl bus = new EventBusImpl(this);
    /**
     * A collection of loggers where change operations on this node or children are logged.
     * @see ChangeEvent
     */
    private final Set<DataOperationLog> loggers = Collections.newSetFromMap(new IdentityHashMap<>());
    /**
     * This set contains all pointers, that are part of the same tree, that point to this node.
     * <br><br>
     * Only using ValuePointer allows better tracking pointers and relations inside the database.
     * In theory, this could allow a feature to automatically delete central data blocks in the tree
     * when no one uses them anymore.
     * <br><br>
     * The referenceManagement is for internal runtime references, which is different to this kind of link.
     */
    private final Set<ValuePointer> pointersToThis = Collections.newSetFromMap(new IdentityHashMap<>());

    /**
     * @param id
     * @throws ContractViolatedException if {@link IDMutable} contract is violated
     * and {@link IDMutable#getUUID()} returns a different uuid than this {@link #uuid}
     */
    public BaseStructure(String id) {
        if (this instanceof IDMutable mutable) {
            this.uuid = UUID.randomUUID();
            if (!mutable.getUUID().equals(this.uuid)) {
                throw new ContractViolatedException("IDMutable.getUUID() is not equal to this.uuid!");
            }
        } else {
            this.uuid = null;
        }

        this.id = id;
    }

    /**
     * @param id
     * @param uuid if this implements {@link IDMutable} specify a custom uuid.
     * @throws ContractViolatedException if {@link IDMutable} contract is violated
     * and {@link IDMutable#getUUID()} returns a different uuid than this {@link #uuid} or if the specified uuid parameter
     * is null.
     *
     * @implNote A constructor where uuid is exposed should be kept protected.
     * All subclasses should define this constructor to allow {@link IDMutable} subclasses.
     */
    protected BaseStructure(String id, @NotNull UUID uuid) {
        if (this instanceof IDMutable && uuid == null) {
            throw new ContractViolatedException("The given uuid is null while this implements IDMutable.");
        }

        this.id = id;
        this.uuid = this instanceof IDMutable ? uuid : null;

        if (this instanceof IDMutable mutable && !mutable.getUUID().equals(uuid)) {
            throw new ContractViolatedException("The provided uuid does not match the return value of IDMutable.getUUID()");
        }
    }

    public final EventBus getEventBus() {
        return this.bus;
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
     *     <li>{@link IdentifierChangeEvent}</li>
     *     <li>{@link ParentChangeEvent}</li>
     * </ul>
     * @param parent can be null if the element, e.g. when it is supposed to be removed from the tree.
     * @throws SetParentNotAllowed if setting the parent is not allowed due to conflicts or other exceptions.
     *                                        This will leave this element and the children elements unchanged.
     *                                        Setting the parent is not allowed in the following cases:
     *                                        <ul>
     *                                            <li>There is a loop in the parents i.e. some parent is reference equal to this.</li>
     *                                            <li>There is a path collision with this element and it cannot be resolved as this is not IDMutable.</li>
     *                                            <li>This element has distant children that now have path collisions
     *                                            with the distant children of the new ID uniqueness context.</li>
     *                                            <li>If this element doesn't adhere to the parent's path format</li>
     *                                            <li>If this element is still a child of another parent.
     *                                            This should enforce that BaseStructures cannot accidentally
     *                                            become children of multiple nodes.</li>
     *                                            <li>If this element is not a child of the new parent.
     *                                            This enforces the child and parent linkage and ensures that
     *                                            this method can simulate a tree state where the parent-child relationship changed
     *                                            to check if it is allowed. In the try-catch-block you should remove the child from the
     *                                            children again if this operation is not supported.</li>
     *                                            <li>If there are wonky illegal states that shouldn't be possible.</li>
     *                                            <li>Implementation of {@link #throwExceptionForIllegalParentChange}</li>
     *                                            <li>If this is IDMutable and the UUID of this is not unique anymore with the neighbors.</li>
     *                                            <li>If something went wrong during {@link #beforeSetParentNull()}</li>
     *                                        </ul>
     */
    public final void setParent(@Nullable final BaseValueHierarchy<?> parent) throws SetParentNotAllowed {
        if (this.parent == parent) return;
        /*
         * declare ALL values of the tree state that might be changed during setParent here at the top
         * to safely revert them if ANY error happens.
         */
        final BaseValueHierarchy<?> originalParent = this.parent;
        final String originalIdentifier = this.id;

        /* check for loop in parents to avoid recursion and stackoverflow */
        for (BaseValueHierarchy<? extends BaseStructure> testParent = parent; testParent != null;
             testParent = ((BaseStructure) testParent).parent) {
            if (testParent == this) {
                throw new SetParentNotAllowed("Detected loop in parent tree. Can't set parent as it results in recursion.");
            }
        }

        /* getChildren() and clearNodeFromMemory() MAY cause exceptions,
         * The only exception that should come from this method is SetParentNotAllowed.
         * This is important, so a caller, which catches SetParentNotAllowed and restores the original tree state,
         * can never cause an unfinished tree state when a different unexpected exception comes from here.
         */
        try {
            /*
             * Check if the tree state is correct.
             * This node must be a child of the new parent and be removed as child of the old parent.
             */
            if (this.parent != parent) {
                if (this.parent != null && this.parent.getChildren().contains(this)) {
                    throw new SetParentNotAllowed("Setting the parent of a BaseStructure to another element," +
                            "while it is still contained in the children of the previous parent, is prohibited to ensure tree integrity.");
                }

                if (parent != null && !parent.getChildren().contains(this)) {
                    throw new SetParentNotAllowed("The new parent needs to have this in its children so the setParent() method can fully simulate" +
                            "a new tree state and check if setting the parent is allowed.");
                }
            }

            /*
             * Automatically call the clear references method in setParent when the parent is changed to null.
             * This is very important so a node cannot be accidentally removed from tree without cleaning up
             * and causing memory leaks.
             * The tree is a closed memory system: wanting to remove from the tree is synonymous with deleting it from memory.
             */
            if (parent == null && originalParent != null) {
                this.beforeSetParentNull();
            }

            /*
             * set parent here to fully simulate a changed tree structure so all methods of the tree work correctly.
             */
            this.parent = parent;
            /* call this here so BaseStructure implementation can also prohibit changing parent to null */
            this.throwExceptionForIllegalParentChange();
        } catch (SetParentNotAllowed e) {
            this.parent = originalParent;
            this.id = originalIdentifier;
            throw e;
        } catch (Exception e) {
            this.parent = originalParent;
            this.id = originalIdentifier;
            throw new SetParentNotAllowed(e);
        }

        /* The safety checks performed here are only relevant when the node is inserted into the tree, not removed. */
        if (parent != null) {
            /*
             * Everything here is in a try-catch statement to ensure
             * that when ANY error happens, we can revert the parent and other changed states,
             * so the tree state stays untouched if setParent is not allowed.
             */
            try {
                /* test if id is allowed with the new parent e.g. path formatting etc. */
                this.throwExceptionForInvalidIDState(this.id);

                BaseValueHierarchy<?> uniquenessContext = this.parent.getPathCollisionContext();
                /* when we have a parent, uniquenessContext could never be null -> exception here means sus stuff! */
                if (uniquenessContext == null) {
                    throw new IllegalStateException("Uniqueness context for resolving path collisions is null while having a parent...");
                }

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
                if (originalParent == null || (this.parent != null && originalParent.getRoot() != this.parent.getRoot())) {
                    this.triggerEvent(new AddToTreeEvent(this));
                }

                //TODO what about this event? Only trigger for non nulls? Do we need this event even?
                /* if this point is reached there was no exception and the operation is successful */
                if (this.parent != originalParent) {
                    this.triggerEvent(new ParentChangeEvent(this, originalParent, this.parent));
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
    }

    /**
     * Allow a subclass to define whether the given parent is structurally allowed or would
     * create an illegal structure endangering data integrity.
     * <br><br>
     * <b>This method is called after {@link #parent} has changed
     * and the check for the tree state with parent-child linkage is correct. This means you can use all methods that rely on a finished tree state.</b>
     * @throws SetParentNotAllowed if setting the parent to the given argument is illegal and endangers structural integrity.
     * This will be called in {@link #setParent(BaseValueHierarchy)},
     * to allow subclasses to indirectly influence the success of that operation without overriding it.
     * @throws RuntimeException this method is allowed to throw any runtime exception, as it will be handled in {@link #setParent(BaseValueHierarchy)}
     * @implNote This is default to ensure ONLY core data modules can override this. It is a core method needed for
     * the structural safety of the tree. Opening access to application data inheriting these base classes makes no sense
     * and could result in overusing/abusing it. There are other methods available for application data to ensure valid data.
     * This here is for the base data tree integrity. If your app data thinks it could create something that creates invalid
     * tree integrity, you are wrong, that would simply be invalid application data you need to validate before setting.
     */
    void throwExceptionForIllegalParentChange() throws SetParentNotAllowed {}

    @Nullable
    public final BaseValueHierarchy<?> getParent() {
        return this.parent;
    }

    /**
     * @param parent
     * @return true if this node is a distant child of the given parent
     * @implNote simply goes through the parents recursively, not much performance cost.
     */
    @NoExceptions
    public final boolean isChildOf(BaseValueHierarchy<? extends BaseStructure> parent) {
        if (parent == null) return false;
        if (this.parent != null && parent != this.parent) {
            return this.parent.isChildOf(parent);
        }

        return this.parent == parent;
    }

    /**
     * Add a logger that logs changes, triggering the {@link ChangeEvent} event.
     * Changes to this and all children are logged.
     * @param log
     */
    public void addLogger(DataOperationLog log) {
        if (log.isEnded()) return;
        this.loggers.add(log);
    }

    public void removeLogger(DataOperationLog manager) {
        this.loggers.remove(manager);
    }

    /**
     * @return the full path including the root.
     * @implNote includes all elements in the tree, even empty path elements.
     */
    @NotNull
    public final ValuePath getFullPath() {
        return Objects.requireNonNull(this.getRelativePath(null));
    }

    /**
     * Appends all path segments together.
     * @param relative
     * @return null if the relative element was not found in the parents or if it is this.
     *         <br><br>
     *         For relative == null returns the full path including the root element.
     *         <br><br>
     *         Otherwise, returns a path that begins at the relative element (exclusive) and ends at this element (inclusive).<br>
     *         The relative path will have the relative's path format at the beginning, but not the id of the relative element.
     * @throws IllegalStateException
     * @throws ContractViolatedException {@link BaseValueHierarchy#getPathSegmentPrefixForChild(BaseStructure)}
     * @implNote includes all elements in the tree, even empty path elements.
     * This behavior should not change as the ValuePath has methods to clean it.
     */
    @Nullable
    public final ValuePath getRelativePath(@Nullable BaseValueHierarchy<?> relative) {
        /* no need to search for this in the parents */
        if (relative == this) return null;

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
        Pair<ID, ValuePath.Format> pair = this.parent.getPathSegmentPrefixForChild(this);
        ValuePath.Format idFormat = this.parent.getPathFormat();
        if (pair.getB() != null) {
            if (pair.getA() == null) {
                throw new IllegalStateException("This is not a child of this.parent according to getIdentifierPrefixForChild().");
            }
            idFormat = pair.getB();
            path.append(this.parent.getPathFormat(), pair.getA());
        }

        path.append(idFormat, this.getID());
    }

    /**
     * Sets the ID's identifier to the given argument,
     * if possible, and resolves path collisions by adjusting the given identifier.
     * <br>
     * Events triggered: <ul><li>{@link IdentifierChangeEvent}</li></ul>
     * <br>
     *
     * @param identifier
     * @throws RuntimeException Changing the identifier is not allowed in the following cases and will throw a RuntimeException:
     * <ul>
     *     <li>This does not implement {@link IDMutable}</li>
     *     <li>When this path segment relative to the parent is not empty and the identifier change makes it empty.<br>
     *     Example: identifier is test and it is changed to "" (empty).<br>
     *     parent["test"] -> parent[""] is allowed<br>
     *     parent.test -> parent is not allowed.<br>
     *     This is because such an empty path segment will be skipped by the path search algorithms and there could be
     *     children exposed that create path collisions with other children.</li>
     *     <li>The UUID returned by {@link IDMutable#getUUID()} is null</li>
     *     <li>The UUID returned by {@link IDMutable#getUUID()} is different than the UUID in the current {@link #id}</li>
     *     <li>The ID does not adhere to the path format of the parent.</li>
     * </ul>
     */
    protected final void _setID(String identifier) throws RuntimeException {
        if (identifier == null) {
            return;
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
            this.id = identifier;
            IdentifierChangeEvent event = new IdentifierChangeEvent(this, originalID, this.id);
            this.triggerEvent(event);
            return;
        }

        BaseValueHierarchy<?> uniquenessContext = this.parent.getPathCollisionContext();
        List<BaseStructure> neighbors = uniquenessContext.getChildrenForPathCollisions();
        if (!neighbors.isEmpty()) {
            identifier = PathUtils.resolvePathCollisions(identifier, this);
        }

        this.throwExceptionForInvalidIDChange(this.id, identifier);
        this.id = identifier;

        IdentifierChangeEvent event = new IdentifierChangeEvent(this, originalID, this.id);
        this.triggerEvent(event);
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
                throw new IllegalStateException("The identifier does not adhere to the parent's prefix path format for this child.");
            } else if (this.parent.getPathFormatPrefixForChildren() == null
                    && !this.parent.getPathFormat().isValidStringForEnclosure(id)) {
                throw new IllegalStateException("The identifier does not adhere to the parent's path format.");
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

    @NoExceptions
    public final String getIdentifier() {
        return this.id;
    }

    @NoExceptions
    public final ID getID() {
        return new ID(this.id, this.uuid);
    }

    @NoExceptions
    public BaseValueHierarchy<?> getRoot() {
        if (this.parent != null) return this.parent.getRoot();

        /* cast should be safe here, going to the top of the parents must be a BaseValueHierarchy */
        return (BaseValueHierarchy<?>) this;
    }


    /* EVENT STUFF */
    /**
     * Records the change event to all the loggers registered in this node and all the parent nodes.
     * @param event
     */
    private void logChangeEvent(ChangeEvent<?> event) {
        for (DataOperationLog manager : this.loggers) {
            manager.recordDataOperation(event.getTrigger(), event.getDataOperation());
        }

        if (this.parent != null) {
            ((BaseStructure)this.parent).logChangeEvent(event);
        }
    }

    private final <T extends Event> void triggerChildEvent(T event) {
        this.bus._dispatchEventFromChildNode(event);
        if (this.parent != null) {
            ((BaseStructure) this.parent).triggerChildEvent(event);
        }
    }

    /**
     * Triggers an event on this element and passes and event to the parents
     * @param event
     * @throws IllegalArgumentException if the trigger of the event is not equal to this.
     */
    protected final <T extends Event> void triggerEvent(T event) {
        if (event.getTrigger() != this) {
            throw new IllegalArgumentException("Cannot trigger event where the trigger is not this.");
        }

        try {
            /*
             * Log changes before triggering events.
             * Events might result in new changes, so first record the source change for correct chronological order.
             */
            if (event instanceof ChangeEvent<?> changeEvent) {
                this.logChangeEvent(changeEvent);
            }
        } catch (Exception e) {
            /*
             * If there is a problem with logging, it is a critical bug, as we cannot revert changes properly.
             * The App needs to crash, so we can quickly fix the underlying issue!
             */
            throw new Error("An exception happened during logging the change. This is a critical bug!", e);
        }

        this.bus._dispatchEventOnThis(event);

        /*
         * Dieser Code wurde in der Gemeinschaft mit Marlon gesegnet (3.10.2024 23:22 Areon Pictures Discord).
         * In nomine Patris et Fili, et Spiritus Sancti. Amen.
         * Die Architektur liegt nun in Gottes Händen. ≽^•⩊•^≼ mowmow
         * FurryFlo war auch dabei.
         */
        if (this.parent != null) {
            ((BaseStructure) this.parent).triggerChildEvent(event);
        }
    }

    /**
     * <h2><b>Call this method before removing a node from the tree to clear it from memory!</b></h2>
     * Template method pattern, also enforce important things that should always happen when removing from the tree.
     * <br><br>
     * Triggers {@link RemoveFromTreeEvent}
     * <br><br>
     * Frees all pointers<br>
     * Clears all event listeners (TODO not sure if this is a good idea?).<br>
     * Clears all referneces in the BaseStructure tree to this.<br>
     * <b>Usage:</b>
     * Call this after an element is removed from tree. Do not call after temporarily removing an element from tree,
     * e.g. setting parent to null for a while.
     */
    protected final void beforeSetParentNull() {
        /* only trigger event once on the element that this method is called */
        this.triggerEvent(new RemoveFromTreeEvent(this));
        this.afterRemoveFromTreeIteration();
    }

    private void afterRemoveFromTreeIteration() {
        //TODO go through ValuePointers and clear them
        this.pointersToThis.clear();
        if (this instanceof BaseValueHierarchy<?> hierarchy) {
            hierarchy.getChildren().forEach(BaseStructure::afterRemoveFromTreeIteration);
        }
    }

    /**
     * @implNote package-private so only ValuePointer can add itself as a pointer to a tree node.
     */
    final void addPointer(ValuePointer<?> pointer) {
        /* TODO slowly I am wondering if I do too many safety checks...
            theoretically ValuePointer handles this, but assuming this here would mean spreading implementation knowledge*/
        if (!pointer.getType().isAssignableFrom(this.getClass())) {
            throw new UnsupportedOperationException("Cannot add a pointer link when the type of the pointer does not match this class.");
        }

        this.pointersToThis.add(pointer);
    }

    /**
     * A pointer needs to call this when it does not point to this anymore.
     * @implNote package-private so only ValuePointer can remove itself as a pointer to a tree node.
     */
    final void removePointer(ValuePointer<?> pointer) {
        this.pointersToThis.remove(pointer);
    }
}