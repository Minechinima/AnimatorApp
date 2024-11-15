package com.minechinima.controller;

import com.minechinima.model.base.BaseValue;
import com.minechinima.model.events.IEventListener;
import com.minechinima.model.events.changes.ValueChangeEvent;
import com.minechinima.utils.MathUtils;

//TODO could be used to accidentally create illegal states like setting parent incorrectly.
public class BaseValueController {
    /* flags */
    public static final int UNDO_MERGE = 1 << 1;
    public static final int DISABLE_PREV_UNDO_MERGE = 1 << 2;

    public static <T> void setValue(BaseValue<T> value, T toSet) {
        setValue(value, toSet, 0);
    }

    public static <T> void setValue(BaseValue<T> value, T toSet, int undoMergeFlags) {
        EventListenerHelper listener = new EventListenerHelper(undoMergeFlags);
        value.addListener(ValueChangeEvent.class, listener);

        try {
            value.set(toSet);
        } finally {
            value.removeListener(listener);
        }
    }

    private static class EventListenerHelper implements IEventListener<ValueChangeEvent> {
        private final int undoMergeFlags;

        public EventListenerHelper(int undoMergeFlags) {
            this.undoMergeFlags = undoMergeFlags;
        }

        public void onEvent(ValueChangeEvent e) {
            if (MathUtils.isBitFlagSet(UNDO_MERGE, undoMergeFlags)) e.getDataOperation().enableMerge();
            if (MathUtils.isBitFlagSet(DISABLE_PREV_UNDO_MERGE, undoMergeFlags)) e.getDataOperation().disableMerginWithPrev();
        }
    }
}
