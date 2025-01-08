package com.minechinima.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleProvider {
    ResourceBundle load(@NotNull Locale locale);
}
