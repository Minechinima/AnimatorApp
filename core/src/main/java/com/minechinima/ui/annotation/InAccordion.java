package com.minechinima.ui.annotation;

public @interface InAccordion {
    String group();
    Class<?> clazz() default Object.class;
}
