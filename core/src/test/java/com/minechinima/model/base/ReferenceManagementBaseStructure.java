package com.minechinima.model.base;

import com.minechinima.model.events.Event;
import com.minechinima.model.events.IEventListener;
import com.minechinima.model.utils.IPointer;

import java.lang.reflect.Field;
import java.util.*;

/**
 * If you want to add a test for a BaseStructure sub class that has new references, you can
 * extend this class and override assignReferences method, but still call super method.
 * @param <T>
 */
public class ReferenceManagementBaseStructure<T extends BaseStructure> {
    protected final T referencee;

    public ReferenceManagementBaseStructure(T referencee) {
        this.referencee = referencee;
    }

    public T getReferencee() {
        return this.referencee;
    }

    public List<BaseStructure> assignReferences() {
        List<BaseStructure> referenced = new ArrayList<>();
        
        DummyBaseStructure s1 = new DummyBaseStructure("");
        DummyBaseStructure s2 = new DummyBaseStructure("");
        DummyBaseStructure s3 = new DummyBaseStructure("");
        DummyBaseStructure s4 = new DummyBaseStructure("");
        DummyBaseStructure s5 = new DummyBaseStructure("");
        DummyBaseStructure s6 = new DummyBaseStructure("");
        DummyBaseStructure s7 = new DummyBaseStructure("");
        referenced.addAll(List.of(s1, s2, s3, s4, s5, s6, s7));
        this.referencee.addListener(Event.class, s1);
        this.referencee.addListener(Event.class, s1);
        this.referencee.addListener(Event.class, s2);
        this.referencee.addListener(Event.class, s2);
        this.referencee.addListener(Event.class, s7);

        this.referencee.addChildListener(Event.class, s3);
        this.referencee.addChildListener(Event.class, s3);
        this.referencee.addChildListener(Event.class, s4);
        this.referencee.addChildListener(Event.class, s4);
        this.referencee.addChildListener(Event.class, s7);

        this.referencee.addPointer(s5);
        s5.point(this.referencee);
        this.referencee.addPointer(s5);
        this.referencee.addPointer(s6);
        s6.point(this.referencee);
        this.referencee.addPointer(s6);
        this.referencee.addPointer(s7);
        s7.point(this.referencee);

        return referenced;
    }

    public boolean hasReference(BaseStructure holder, BaseStructure reference) throws NoSuchFieldException, IllegalAccessException {
        Field referencesToThisField = BaseStructure.class.getDeclaredField("referencesToThis");
        Field listenersField = BaseStructure.class.getDeclaredField("eventListeners");
        Field childEventListenersField = BaseStructure.class.getDeclaredField("childEventListeners");
        Field pointersField = BaseStructure.class.getDeclaredField("pointers");

        referencesToThisField.setAccessible(true);
        listenersField.setAccessible(true);
        childEventListenersField.setAccessible(true);
        pointersField.setAccessible(true);

        Set<BaseStructure> referencesList = (Set<BaseStructure>) referencesToThisField.get(holder);
        Set<BaseStructure> isReferencedList = (Set<BaseStructure>) referencesToThisField.get(reference);

        Map<Class<Event>, Set<IEventListener>> eventListeners = (Map<Class<Event>, Set<IEventListener>>) listenersField.get(holder);
        Map<Class<Event>, Set<IEventListener>> childEventListeners = (Map<Class<Event>, Set<IEventListener>>) listenersField.get(holder);
        Set<IPointer> pointers = (Set<IPointer>) pointersField.get(holder);

        return referencesList.contains(reference)
                || isReferencedList.contains(holder)
                || this.collectionContains(eventListeners.values(), reference)
                || this.collectionContains(childEventListeners.values(), reference)
                || this.collectionContains(pointers, reference);
    }

    public boolean collectionContains(Collection<?> collection, Object o) {
        for (Object item : collection) {
            if (item instanceof Collection<?> collection1 && this.collectionContains(collection1, o)) {
                return true;
            }

            if (item == o) return true;
        }

        return false;
    }
}
