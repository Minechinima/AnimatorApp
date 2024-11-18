package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.ListType;
import com.minechinima.data.types.MapType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.dataoperations.datainterfaces.ListElement;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.dataoperations.datainterfaces.IValueList;
import com.minechinima.model.events.changes.ListAddEvent;
import com.minechinima.model.events.changes.ListIndexChangeEvent;
import com.minechinima.model.events.changes.ListRemoveEvent;
import com.minechinima.model.dataoperations.datainterfaces.IDMutable;
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
    /**
     * Never contains the same reference equal object twice.
     * Every object gets a new UUID so an instance can be retrieved no matter what it's index is.
     */
    private final List<ListElement<T>> children = new ArrayList<>();

    public BaseValueOrderedCollection(String id, ValuePath.Format pathFormat) {
        super(id, pathFormat);
    }

    protected BaseValueOrderedCollection(String id, UUID uuid, ValuePath.Format pathFormat) {
        super(id, uuid, pathFormat);
    }

    @Override
    public final List<T> getChildren() {
        List<T> list = new ArrayList<>();
        this.children.forEach(pair -> list.add(pair.value()));
        return list;
    }

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
        return this.indexOf(o) >= 0;
    }

    @Override
    public boolean isDirectChild(BaseStructure child) {
        return this.indexOf(child) >= 0;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.getChildren().iterator();
    }

    @NotNull
    public final T get(int index) throws IndexOutOfBoundsException {
        return this.children.get(index).value();
    }

    @Nullable
    public final T getByListUUID(UUID uuid) {
        for (ListElement<T> e : this.children) {
            if (e.uuid().equals(uuid)) return e.value();
        }

        return null;
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

        T prev = this.children.get(index).value();
        if (this.remove(index) != prev) throw new IllegalStateException("wtf...");
        this.add(index, element);

        return prev;
    }

    /**
     * Moves the element at the specified index to the newIndex.
     * @param index
     * @param newIndex
     */
    public final void move(int index, int newIndex) {
        if (index == newIndex) return;
        if (index >= this.children.size() || index < 0 || newIndex < 0) throw new IndexOutOfBoundsException();

        this.moveByListUUID(this.children.get(index).uuid(), newIndex);
    }

    public final void moveByListUUID(UUID uuid, int newIndex) {
        int index = this.indexOfListUUID(uuid);
        if (index == newIndex || index < 0) return;
        if (index >= this.children.size() || newIndex < 0) throw new IndexOutOfBoundsException();

        newIndex = Math.min(this.children.size() - 1, newIndex);

        ListElement<T> pair = this.children.get(index);
        this.children.remove(pair);
        this.children.add(newIndex, pair);
        this.triggerEvent(new ListIndexChangeEvent(this, index, newIndex + 1, pair));
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

    @Override
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
    public boolean addListElement(int index, ListElement<T> element) {
        if (index < 0 || index > this.children.size()) {
            throw new IndexOutOfBoundsException();
        }

        if (this.containsListUUID(element.uuid())) {
            return false;
        }

        this.throwExceptionForIllegalChild(element.value());
        this.children.add(index, element);
        try {
            element.value().setParent(this);
        } catch (SetParentNotAllowed e) {
            this.children.remove(element);
            throw new RuntimeException(e);
        }

        this.triggerEvent(new ListAddEvent(this, element.value()));
        return true;
    }

    /**
     * @param value
     * @throws IllegalArgumentException when the same reference equal value is in the list.
     * @throws NullPointerException when the value is null
     */
    @Override
    public final boolean add(@NotNull T value) {
        this.throwExceptionForIllegalChild(value);
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
        ListElement<T> value = this.children.get(index);

        this.children.remove(index);
        try {
            value.value().setParent(null);
        } catch (SetParentNotAllowed e) {
            this.children.add(index, value);
            throw new UnsupportedOperationException(e);
        }

        this.triggerEvent(new ListRemoveEvent(this, index, value));
        return value.value();
    }

    /**
     * @param uuid
     * @return the element with the uuid if it was present and could be removed.
     *         null if there was no element with the uuid or if it could not be removed.
     * @throws RuntimeException any exception can occur if the parent of the removed element cannot be cleared.
     */
    @Override
    @Nullable
    public final T removeByListUUID(UUID uuid) {
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
    public boolean clear() {
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
        return this.indexOfRange(o, 0, this.children.size());
    }

    public final int indexOfListUUID(UUID uuid) {
        return this.indexOfRange(uuid, 0, this.children.size());
    }

    @Override
    public final boolean containsListUUID(UUID uuid) {
        return this.indexOfListUUID(uuid) >= 0;
    }

    @Nullable
    public final UUID listUUIDOfIndex(int index) {
        if (index < 0 || index >= this.children.size()) return null;
        return this.children.get(index).uuid();
    }

    @Nullable
    public final UUID listUUIDOf(T o) {
        int i = this.indexOf(o);
        if (i >= 0) return this.children.get(i).uuid();
        return null;
    }

    final int indexOfRange(BaseStructure o, int start, int end) {
        if (o == null) {
            for (int i = start; i < end; i++) {
                if (this.children.get(i).value() == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (o == this.children.get(i).value()) {
                    return i;
                }
            }
        }
        return -1;
    }

    final int indexOfRange(UUID uuid, int start, int end) {
        if (uuid == null) {
            for (int i = start; i < end; i++) {
                if (this.children.get(i).uuid() == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (uuid.equals(this.children.get(i).uuid())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public ListElement<T> listElementFromData(MapType child) throws BaseTypeCannotBeParsed {
        String id = child.getStringType("id") != null ? child.getStringType("id").getValue() : "";
        UUID uuid = UUIDUtils.readUUIDOrCreateRandom(child, "uuid");
        UUID valueUUID = UUIDUtils.readUUID(child, "value_uuid");
        BaseType<?> childData = child.get("data");
        return new ListElement<>(uuid, this.instantiate(id, valueUUID, childData));
    }

    public MapType listElementToData(ListElement<T> element) {
        MapType map = new MapType(new HashMap<>());
        map.getValue().put("id", new StringType(element.value().getIdentifier()));
        map.getValue().put("data", element.value().toData());

        if (element.value() instanceof IDMutable mutable) {
            UUIDUtils.putUUID(map, "value_uuid", mutable.getUUID());
        }

        return UUIDUtils.putUUID(map, "uuid", element.uuid());
    }

    @NotNull
    protected abstract T instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed;

    @Override
    public ListType toData() {
        List<BaseType<?>> list = new ArrayList<>();
        for (ListElement<T> child : this.children) {
            list.add(this.listElementToData(child));
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