package com.minechinima.model.values.base;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.ListType;
import com.minechinima.data.types.MapType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.base.BaseMutableValueHierarchy;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.base.utils.BaseOperationsUtils;
import com.minechinima.model.base.utils.IBaseValueCollection;
import com.minechinima.model.logging.datainterfaces.ListElement;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.logging.datainterfaces.IValueList;
import com.minechinima.model.events.types.changes.ListAddEvent;
import com.minechinima.model.events.types.changes.ListIndexChangeEvent;
import com.minechinima.model.events.types.changes.ListRemoveEvent;
import com.minechinima.model.logging.datainterfaces.IDMutable;
import com.minechinima.utils.UUIDUtils;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Common list index operations.
 * @param <T>
 */
public abstract class BaseValueOrderedCollection<T extends BaseStructure> extends BaseMutableValueHierarchy<T>
        implements Iterable<T>, IBaseValueCollection<T>, IValueList<T> {
    private final List<T> children = new ArrayList<>();
    /**
     * Map the children to a UUID. This allows addressing children independent of the mutable list index.
     */
    private final BiMap<UUID, T> listUUIDMapping = HashBiMap.create();

    public BaseValueOrderedCollection(String id, ValuePath.Format pathFormat) {
        super(id, pathFormat);
    }

    protected BaseValueOrderedCollection(String id, UUID uuid, ValuePath.Format pathFormat) {
        super(id, uuid, pathFormat);
    }

    @Override
    public final List<T> getChildren() {
        return List.copyOf(this.children);
    }

    @NotNull
    protected abstract T instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed;

    @Override
    public int size() {
        return this.children.size();
    }

    @Override
    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    @Override
    public boolean contains(T o) {
        if (!this.children.contains(o)) return false;

        if (!this.listUUIDMapping.containsValue(o)) {
            throw new IllegalStateException("Child is contained but no UUID mapping.");
        }

        return true;
    }

    @Override
    public final boolean containsListUUID(@NotNull UUID uuid) {
        return this.listUUIDMapping.containsKey(uuid);
    }

    @Override
    public boolean isDirectChild(BaseStructure child) {
        return this.children.contains(child);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.getChildren().iterator();
    }

    @NotNull
    public final T get(int index) throws IndexOutOfBoundsException {
        return this.children.get(index);
    }

    @Nullable
    public final T getByListUUID(UUID uuid) {
        T child = this.listUUIDMapping.get(uuid);
        if (child != null && !this.children.contains(child)) {
            throw new IllegalStateException("Child is mapped to a UUID but is not in the children");
        }
        return child;
    }

    /**
     *
     * @param index
     * @param element
     * @return the previous element at that position
     * @throws NullPointerException is element is null
     * @throws IndexOutOfBoundsException
     */
    @NotNull
    public final T set(int index, @NotNull T element) {
        if (element == null) throw new NullPointerException();
        if (index >= this.children.size() || index < 0) throw new IndexOutOfBoundsException();

        T prev = this.children.get(index);
        if (this.remove(index) != prev) throw new IllegalStateException("wtf...");
        this.add(index, element);

        return prev;
    }

    /**
     * Moves the element at the specified index to the newIndex.
     * @param index
     * @param newIndex
     */
    @MustBeInvokedByOverriders
    public void move(int index, int newIndex) {
        if (index == newIndex) return;
        if (index >= this.children.size() || index < 0
                || newIndex >= this.children.size() || newIndex < 0) throw new IndexOutOfBoundsException();

        UUID uuid = this.listUUIDOfIndex(index);
        if (uuid == null) throw new IllegalStateException("No UUID mapping for a valid index.");

        T child = this.children.get(index);
        if (child == null) throw new IllegalStateException("A child is null.");

        this.mutate(() -> {
            /*
             * Just move around the child in the internal list.
             * Path collisions should be impossible, as no new path is created. We can only move the child to an index
             * that existed previously e.g. move index 1 to 2 [0,1,2] -> [0,2] -> [0,2,1]
             */
            if (!this.children.remove(child)) return false;
            this.children.add(newIndex, child);
            return true;
        }, () -> {
            if (this.children.contains(child) && !this.children.remove(child)) return false;
            this.children.add(index, child);
            return true;
        }, () -> new ListIndexChangeEvent(this, index, newIndex, new ListElement<>(uuid, child)));
    }

    @Override
    public final void moveByListUUID(@NotNull UUID uuid, int newIndex) {
        int index = this.indexOfListUUID(uuid);
        this.move(index, newIndex);
    }

    /**
     *
     * @param index
     * @param element
     * @throws IndexOutOfBoundsException
     * @throws IllegalArgumentException if this element is already in the list
     * @throws NullPointerException if the element is null
     */
    public final boolean add(int index, @NotNull T element) {
        return this.addListElement(index, new ListElement<>(UUID.randomUUID(), element));
    }

    public final boolean addChild(T child) {
        return this.add(child);
    }

    /**
     * @throws IndexOutOfBoundsException
     * @throws NullPointerException when the value is null
     * @throws IllegalArgumentException when the same reference equal value is in the list.
     * @throws RuntimeException if setting the parent does not work.
     */
    @Override
    @MustBeInvokedByOverriders
    public boolean addListElement(int index, @NotNull ListElement<T> element) {
        if (index < 0 || index > this.children.size()) {
            throw new IndexOutOfBoundsException();
        }

        if (this.children.contains(element.value()) || this.containsListUUID(element.uuid())) {
            return false;
        }

        final BaseValueHierarchy prevParent = element.value().getParent();

        this.mutate(() -> {
                    /* map the uuid to simulate a full tree state and correctly check whether child can be added */
                    this.listUUIDMapping.put(element.uuid(), element.value());
                    this.children.add(index, element.value());
                    element.value().setParent(this);
                    return true;
                },
                () -> {
                    this.listUUIDMapping.remove(element.uuid());
                    if (this.children.contains(element.value()) && !this.children.remove(element.value())) return false;
                    element.value().setParent(prevParent);
                    return true;
                },
                () -> new ListAddEvent(this, element.value()));
        return true;
    }

    /**
     * @param value
     * @throws IllegalArgumentException when the same reference equal value is in the list.
     * @throws NullPointerException when the value is null
     */
    @Override
    public final boolean add(@NotNull T value) {
        return this.add(this.children.size(), value);
    }

    /**
     *
     * @param index
     * @return the element previously at the specified index. Might be null when the parent could not be set to null.
     * @throws IndexOutOfBoundsException
     * @throws RuntimeException any exception can occur when trying to clear the node from memory.
     */
    @Nullable
    @MustBeInvokedByOverriders
    public T remove(int index) {
        if (index >= this.children.size() || index < 0) throw new IndexOutOfBoundsException();
        /*
         * Put the removal code in here, so it is more robust by first removing the actual child from the children
         * that is used for the tree.
         */
        T child = this.children.get(index);
        UUID uuid = this.listUUIDOf(child);
        if (uuid == null) throw new NullPointerException();

        ListRemoveEvent e = BaseOperationsUtils.removeChildOrThrow(child, this.children, () -> new ListRemoveEvent(this, index, new ListElement<>(uuid, child)));


        this.mutate(() -> {
                if (!this.children.remove(child)) return false;
                child.setParent(null);
                this.listUUIDMapping.inverse().remove(child);
                return true;
            }, () -> {
                if (!this.children.contains(child)) {
                    this.children.add(index, child);
                }
                this.listUUIDMapping.put(uuid, child);
                child.setParent(this);
                return true;
            }, () -> new ListRemoveEvent(this, index, new ListElement<>(uuid, child)));

        return child;
    }

    /**
     * @param uuid
     * @return the element with the uuid if it was present and could be removed.
     *         null if there was no element with the uuid or if it could not be removed.
     * @throws RuntimeException any exception can occur if the parent of the removed element cannot be cleared.
     */
    @Override
    @Nullable
    public final T removeByListUUID(@NotNull UUID uuid) {
        T e = this.getByListUUID(uuid);
        if (e == null) return null;
        this.remove(e);
        return e;
    }

    /**
     * @param o
     * @throws RuntimeException any exception can occur if the parent of the removed element cannot be cleared.
     */
    @Override
    public final boolean remove(T o) {
        int i = this.indexOf(o);
        if (i >= 0) this.remove(i);
        return i >= 0;
    }

    @Override
    public final boolean removeChild(T child) {
        return this.remove(child);
    }

    /**
     * @throws RuntimeException any exception can occur if the parent of the removed element cannot be cleared.
     */
    @Override
    public final boolean clear() {
        List<T> oldChildren = this.getChildren();

        for (T child : oldChildren) {
            this.remove(child);
        }

        return this.size() == 0;
    }

    /**
     * @param o
     * @return the index of the given element. -1 if it was not found.
     */
    public final int indexOf(BaseStructure o) {
        if (o == null) return -1;
        return this.children.indexOf(o);
    }

    public final int indexOfListUUID(UUID uuid) {
        if (uuid == null) return -1;

        T child = this.listUUIDMapping.get(uuid);
        if (child == null && this.listUUIDMapping.containsKey(uuid)) {
            throw new IllegalStateException("A UUID was found in the list but the mapped child is not present in the children");
        }
        int i = this.children.indexOf(child);

        if (i < 0) {
            throw new IllegalStateException("Could not find the index of a mapped child.");
        }

        return i;
    }

    @Nullable
    public final UUID listUUIDOfIndex(int index) {
        if (index < 0 || index >= this.children.size()) return null;
        UUID uuid = this.listUUIDMapping.inverse().get(this.children.get(index));
        if (uuid == null) {
            throw new IllegalStateException("A child was assigned to the index but the UUID mapping is missing.");
        }
        return uuid;
    }

    @Nullable
    @Override
    public final UUID listUUIDOf(T o) {
        if (o == null) return null;

        if (!this.children.contains(o)) {
            return null;
        }

        UUID uuid = this.listUUIDMapping.inverse().get(o);
        if (uuid == null) {
            throw new IllegalStateException("A child was assigned to the index but the UUID mapping is missing.");
        }
        return uuid;
    }

    public @NotNull ListElement<T> listElementFromData(@NotNull MapType child) throws BaseTypeCannotBeParsed {
        String id = child.getStringType("id") != null ? child.getStringType("id").getValue() : "";
        UUID uuid = UUIDUtils.readUUIDOrCreateRandom(child, "uuid");
        UUID valueUUID = UUIDUtils.readUUIDorNull(child, "value_uuid");
        BaseType<?> childData = child.get("data");
        return new ListElement<>(uuid, this.instantiate(id, valueUUID, childData));
    }

    public @NotNull MapType listElementToData(@NotNull ListElement<T> element) {
        MapType map = new MapType(new HashMap<>());
        map.getValue().put("id", new StringType(element.value().getIdentifier()));
        map.getValue().put("data", element.value().toData());

        if (element.value() instanceof IDMutable mutable) {
            UUIDUtils.putUUID(map, "value_uuid", mutable.getUUID());
        }

        return UUIDUtils.putUUID(map, "uuid", element.uuid());
    }

    @Override
    public ListType toData() {
        List<BaseType<?>> list = new ArrayList<>();
        for (T child : this.children) {
            UUID uuid = this.listUUIDMapping.inverse().get(child);
            /* dont crash here, just save the correct children, allows cleanup via serialization */
            if (uuid == null) continue;
            list.add(this.listElementToData(new ListElement<>(uuid, child)));
        }
        return new ListType(list);
    }

    @Override
    public void fromData(BaseType<?> data) {
        ListType list;
        if ((list = data.asListType()) == null) return;
        this.clear();

        for (int i = 0; i < list.size(); i++) {
            MapType child;
            if ((child = list.get(i).asMapType()) == null) continue;

            try {
                this.addListElement(this.children.size(), this.listElementFromData(child));
            } catch (BaseTypeCannotBeParsed e) { }
        }
    }
}