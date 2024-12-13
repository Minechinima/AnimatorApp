package com.minechinima.ui.annotation;

import com.minechinima.ui.elements.UIElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface InGui {
    Class<? extends IUIHandler<?>> entrypoint();
    Class<? extends UIElement> as();
    String translationPrefix();
}
