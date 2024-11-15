package com.minechinima.model.base;

import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class BaseValueOrderedMap<T extends BaseStructure> extends BaseValueOrderedCollection<T> {
    public BaseValueOrderedMap(String id) {
        super(id, ValuePath.Format.MAP);
    }

    protected BaseValueOrderedMap(String id, UUID uuid) {
        super(id, uuid, ValuePath.Format.MAP);
    }

    @Nullable
    public T get(String id) {
        for (T child : this.getChildren()) {
            if (child.getIdentifier().isEmpty()) continue;
            if (child.getIdentifier().equals(id)) return child;
        }

        return null;
    }

    @Nullable
    public T get(UUID uuid) {
        for (T child : this.getChildren()) {
            if (child.uuid != null && child.uuid.equals(uuid)) {
                return child;
            }
        }

        return null;
    }

    /**
     * @param id
     * @throws RuntimeException {@link BaseValueOrderedCollection#remove(BaseStructure)}
     */
    public boolean remove(String id) {
        for (T child : this.getChildren()) {
            if (child.getIdentifier().equals(id)) {
                return this.remove(child);
            }
        }

        return false;
    }

    /**
     * @param id
     * @throws RuntimeException {@link BaseValueOrderedCollection#remove(BaseStructure)}
     */
    public boolean remove(UUID id) {
        for (T child : this.getChildren()) {
            if (child.uuid != null && child.uuid.equals(id)) {
                return this.remove(child);
            }
        }

        return false;
    }
}
