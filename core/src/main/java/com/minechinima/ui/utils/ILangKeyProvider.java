package com.minechinima.ui.utils;

import org.jetbrains.annotations.Nullable;

public interface ILangKeyProvider {
    String title();
    @Nullable
    default String tooltip() {
        return null;
    }
}
