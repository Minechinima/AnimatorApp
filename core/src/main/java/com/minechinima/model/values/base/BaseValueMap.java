package com.minechinima.model.values.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.MapType;
import com.minechinima.data.types.StringType;
import com.minechinima.model.base.BaseMutableValueHierarchy;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.base.utils.BaseOperationsUtils;
import com.minechinima.model.base.utils.IBaseValueCollection;
import com.minechinima.model.logging.datainterfaces.IDMutable;
import com.minechinima.model.logging.datainterfaces.IValueMap;
import com.minechinima.model.events.types.changes.ValueAddEvent;
import com.minechinima.model.events.types.changes.ValueRemoveEvent;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.utils.UUIDUtils;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Value hierarchy where the children can be removed and added dynamically.
 * This class exposes common adding and removing operations, so it can make things like generalized undo redo operations easier.
 * <br>The order of the children does not matter.
 * @param <T> the type of the children.
 */
public abstract class BaseValueMap<T extends BaseStructure> extends BaseMutableValueHierarchy<T>
        implements IBaseValueCollection<T>, IValueMap<T> {
    private final Set<T> children = Collections.newSetFromMap(new IdentityHashMap<>());

    public BaseValueMap(String id) {
        super(id, ValuePath.Format.MAP);
    }

    protected BaseValueMap(String id, UUID uuid) {
        super(id, uuid, ValuePath.Format.MAP);
    }

    /**
     * final to force subclasses to call setters from this class, so this class can define important stuff like change events
     * to always track changes.
     * @return
     */
    @Override
    public final Set<T> getChildren() {
        return new HashSet<>(this.children);
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
        return this.children.contains(o);
    }

    @Override
    public boolean contains(String id) {
        return this.get(id) != null;
    }

    @Override
    public boolean contains(@NotNull UUID uuid) {
        return this.get(uuid) != null;
    }

    @Override
    public boolean isDirectChild(BaseStructure child) {
        return this.children.contains(child);
    }

    @Nullable
    public final T get(String id) {
        for (T child : this.children) {
            if (child.getIdentifier().equals(id)) {
                return child;
            }
        }

        return null;
    }

    @Nullable
    public final T get(UUID uuid) {
        for (T child : this.children) {
            if (child instanceof IDMutable mutable
                    && mutable.getUUID().equals(uuid)) {
                return child;
            }
        }

        return null;
    }

    /**
     * Add the element to this map.
     * <br><br>
     * Possible Events triggered:
     * <ul>
     *     <li>Events of {@link BaseStructure#setParent(BaseValueHierarchy)}</li>
     *     <li>{@link ValueAddEvent}</li>
     * </ul>
     * @param value null is not allowed
     * @return true when the element could be added.
     * @throws IllegalArgumentException if the same reference equal value is already contained in the children.
     * @throws NullPointerException if the value is null
     */
    @Override
    @MustBeInvokedByOverriders
    public boolean add(@NotNull T value) {
        BaseOperationsUtils.addChildOrThrow(value, this, this.children);
        this.triggerEvent(new ValueAddEvent(this, value));
        return true;
    }

    @Override
    public final boolean addChild(T child) {
        return this.add(child);
    }

    /* REMOVING OPERATIONS */

    @Nullable
    public final T remove(@NotNull UUID uuid) {
        T child = this.get(uuid);
        if (child != null) this.remove(child);
        return child;
    }

    @Nullable
    public final T remove(String id) {
        T child = this.get(id);
        if (child != null) this.remove(child);
        return child;
    }

    /**
     * Possible Events triggered:<br>
     * <ul>
     *     <li>{@link ValueRemoveEvent}</li>
     * </ul>
     * @param child
     * @return true if the element was contained in the map and successfully removed.
     */
    @Override
    @MustBeInvokedByOverriders
    public boolean remove(T child) {
        if (!this.children.contains(child)) return false;
        BaseOperationsUtils.removeChildOrThrow(child, this.children);
        this.triggerEvent(new ValueRemoveEvent(this, child));
        return true;
    }

    @Override
    public final boolean removeChild(T child) {
        return this.remove(child);
    }

    @Override
    public final boolean clear() {
        List<T> oldChildren = new ArrayList<>(this.children);

        for (T child : oldChildren) {
            this.remove(child);
        }

        return this.size() == 0;
    }

    /* SERIALIZATION */

    @Override
    @NotNull
    public T valueFromData(MapType data) throws BaseTypeCannotBeParsed {
        if (data.get("data") == null || data.get("id") == null) {
            throw new BaseTypeCannotBeParsed("");
        }

        String id = data.getStringType("id") != null ? data.getStringType("id").getValue() : "";
        UUID uuid = UUIDUtils.readUUIDorNull(data, "uuid");
        return this.instantiate(id, uuid, data.get("data"));
    }

    public MapType valueToData(T value) {
        MapType map = new MapType(new HashMap<>());
        map.getValue().put("id", new StringType(value.getIdentifier()));
        map.getValue().put("data", value.toData());
        if (value instanceof IDMutable mutable) {
            return UUIDUtils.putUUID(map, "uuid", mutable.getUUID());
        }
        return map;
    }

    @NotNull
    protected abstract T instantiate(String id, @Nullable UUID uuid, BaseType<?> data);

    @Override
    public void fromData(BaseType<?> data) {
        MapType mapType;
        if ((mapType = data.asMapType()) == null) return;

        /* use the clear and add methods that trigger events to also capture undo redo steps for serialization */
        this.clear();
        for (Map.Entry<String, BaseType<?>> entry : mapType) {
            if (!entry.getValue().isMap()) continue;
            try {
                MapType child = entry.getValue().asMapType();
                child.getValue().put("id", new StringType(entry.getKey()));
                this.add(this.valueFromData(entry.getValue().asMapType()));
            } catch (BaseTypeCannotBeParsed e) { }
        }
    }

    @Override
    public MapType toData() {
        Map<String, BaseType<?>> children = new HashMap<>();
        for (T child : this.children) {
            MapType value = this.valueToData(child);
            /* optimize space, no need to store id twice. */
            value.getValue().remove("id");
            children.put(child.getIdentifier(), this.valueToData(child));
        }
        return new MapType(children);
    }
}
