package com.minechinima.model.values.lists;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.base.BaseValueList;
import com.minechinima.model.logging.datainterfaces.ListElement;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.utils.IValuePointerConstraint;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class ValuePointerList<T extends BaseStructure> extends BaseValueList<ValuePointer<T>> implements IValuePointerConstraint {
    private final BaseValueHierarchy<? extends BaseStructure> relative;
    private final boolean nullPointerAllowed;
    private final boolean duplicatePointerTargetsAllowed;
    private final Class<T> type;

    public ValuePointerList(String id, Class<T> type, BaseValueHierarchy<? extends BaseStructure> relative) {
        this(id, type, relative, false, false);
    }

    public ValuePointerList(String id, Class<T> type, BaseValueHierarchy<? extends BaseStructure> relative,
                            boolean nullPointerAllowed, boolean duplicatePointerTargetsAllowed) {
        super(id);
        this.type = type;
        this.relative = relative;
        this.nullPointerAllowed = nullPointerAllowed;
        this.duplicatePointerTargetsAllowed = duplicatePointerTargetsAllowed;
    }

    protected ValuePointerList(String id, UUID uuid, Class<T> type, BaseValueHierarchy<? extends BaseStructure> relative) {
        this(id, uuid, type, relative, false, false);
    }

    protected ValuePointerList(String id, UUID uuid, Class<T> type, BaseValueHierarchy<? extends BaseStructure> relative,
                               boolean nullPointerAllowed, boolean duplicatePointerTargetsAllowed) {
        super(id, uuid);
        this.type = type;
        this.relative = relative;
        this.nullPointerAllowed = nullPointerAllowed;
        this.duplicatePointerTargetsAllowed = duplicatePointerTargetsAllowed;
    }

    @Override
    public boolean nullPointerAllowed() {
        return this.nullPointerAllowed;
    }

    @Override
    public boolean duplicatePointerTargetsAllowed() {
        return this.duplicatePointerTargetsAllowed;
    }

    @Override
    public void callbackOnNullPointer(ValuePointer ptr) {
        this.remove(ptr);
    }

    /**
     *
     * @param pointerTarget
     * @throws
     */
    public void addPointer(T pointerTarget) {
        this.addPointer(this.size(), pointerTarget);
    }

    public void addPointer(int index, T pointerTarget) {
        ValuePointer<T> p = new ValuePointer<>("", this.type, this.relative);
        p.set(pointerTarget);
        if (p.getReference() == pointerTarget) {
            this.addListElement(index, new ListElement<>(UUID.randomUUID(), p));
        } else {
            throw new RuntimeException("Could not set pointer target.");
        }
    }

    @Override
    public boolean addListElement(int index, ListElement<ValuePointer<T>> element) {
        if (element.value().getReferenceFrame() != this.relative) {
            throw new IllegalArgumentException("Trying to add a pointer to a pointerlist with a different relative target.");
        }

        if (!this.type.isAssignableFrom(element.value().getType())) {
            throw new IllegalArgumentException("Cannot add a pointer with a different type than this pointer list.");
        }

        return super.addListElement(index, element);
    }

    @Override
    protected ValuePointer<T> instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        ValuePointer<T> valuePointer = new ValuePointer<>(id, this.type, this.relative);
        valuePointer.fromData(data);
        return valuePointer;
    }

    public List<T> getPointerTargets() {
        List<T> targets = new ArrayList<>();
        for (ValuePointer<T> pointer : this.getChildren()) {
            targets.add(pointer.getReference());
        }
        return targets;
    }

    public List<T> getPointerTargetsSorted(Comparator<T> comparator) {
        List<T> targets = this.getPointerTargets();
        targets.sort(comparator);
        return targets;
    }

    public List<T> getSortedByIdentifier() {
        return this.getPointerTargetsSorted(Comparator.comparing(BaseStructure::getIdentifier));
    }
}
