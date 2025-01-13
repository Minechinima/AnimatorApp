package com.minechinima.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

public class ClasspathBundleProvider implements ResourceBundleProvider {
    private final String baseName;
    private final Class<?> origin;

    public ClasspathBundleProvider(@NotNull String baseName, @NotNull Class<?> origin) {
        assert baseName != null;
        assert origin != null;

        this.baseName = baseName;
        this.origin = origin;
    }

    @Override
    public ResourceBundle load(@NotNull Locale locale) {
        assert locale != null;

        return ResourceBundle.getBundle(baseName, locale, origin.getClassLoader());
    }
}
