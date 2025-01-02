package com.minechinima.ui.annotation;

import com.minechinima.ui.elements.UIGroup;

public @interface InUIGroup {
    String key();
    Class<? extends UIGroup> as();
}
