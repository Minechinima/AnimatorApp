package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.events.Event;
import com.minechinima.model.events.IChildEventListener;
import com.minechinima.model.events.IEventListener;
import com.minechinima.model.utils.IPointer;
import org.jetbrains.annotations.TestOnly;

import java.util.UUID;

@TestOnly
final class DummyBaseStructure extends BaseStructure implements IEventListener<Event>, IChildEventListener, IPointer<BaseStructure> {
    private BaseStructure pointerTarget;

    DummyBaseStructure(String identifier) {
        super(identifier);
    }

    DummyBaseStructure(String id, UUID uuid) {
        super(id, uuid);
    }

    @Override
    public BaseType toData() {
        return null;
    }

    @Override
    public void fromData(BaseType data) {

    }

    @Override
    public void onChildEvent(Event event) {

    }

    @Override
    public void onEvent(Event event) {

    }

    @Override
    public void point(BaseStructure target) {
        if (target == null && this.pointerTarget != null) this.pointerTarget.removePointer(this);
        this.pointerTarget = target;
        if (target != null) target.addPointer(this);
    }

    @Override
    public BaseStructure getReference() {
        return this.pointerTarget;
    }
}
