package com.minechinima.model.values.lists;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.base.BaseValueList;
import com.minechinima.model.logging.datainterfaces.ListElement;
import com.minechinima.model.base.ValuePointer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class ValuePointerList<T extends BaseStructure> extends BaseValueList<ValuePointer<T>> {
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

    /**
     *
     * @param pointerTarget
     * @throws
     */
    public void addPointer(T pointerTarget) {
        ValuePointer<T> p = new ValuePointer<>("", this.type, this.relative);
        p.set(pointerTarget);
        if (p.get() == pointerTarget) {
            this.addPointer(this.size(), pointerTarget);
        } else {
            throw new RuntimeException("Could not set pointer target.");
        }

    }

    public void addPointer(int index, T pointerTarget) {
        ValuePointer<T> p = new ValuePointer<>("", this.type, this.relative);
        p.set(pointerTarget);
        if (p.get() == pointerTarget) {
            this.addListElement(index, new ListElement<>(UUID.randomUUID(), p));
        } else {
            throw new RuntimeException("Could not set pointer target.");
        }
    }

    @Override
    public boolean addListElement(int index, ListElement<ValuePointer<T>> element) {
        if (element.value().getRelative() != this.relative) {
            throw new IllegalArgumentException("Trying to add a pointer to a pointerlist with a different relative target.");
        }

        if (!this.nullPointerAllowed) {
            if (element.value().get() == null) {
                return false;
            }

            //TODO memory leak
            Consumer<T> consumer = v -> { if (v == null) this.remove(element.value()); };
            element.value().addCallbackBeforeSet(consumer);
            //TODO element.value().addListener(RemoveFromTreeEvent.class, e -> element.value().removeCallbackBeforeSet(consumer));
        }

        if (!this.duplicatePointerTargetsAllowed) {
            if (this.getPointerTargets().contains(element.value().getReference())) {
                return false;
            }

            Consumer<T> consumer = v -> { if (this.getPointerTargets().contains(v)) this.remove(element.value()); };
            element.value().addCallbackBeforeSet(consumer);
            //TODO element.value().addListener(RemoveFromTreeEvent.class, e -> element.value().removeCallbackBeforeSet(consumer));
        }

        return super.addListElement(index, element);
    }

    @Override
    public boolean canAdd(int index, ListElement<Object> element) throws UserException {
        if (element.value() instanceof ValuePointer<?> ptr
                && ptr.getType().equals(this.type)) {
            return true;
        }
        return false;
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
