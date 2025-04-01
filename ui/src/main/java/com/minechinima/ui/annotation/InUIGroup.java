package com.minechinima.ui.annotation;


public @interface InUIGroup {
    String key();
    Class<? extends IUIGroup> as();
}
