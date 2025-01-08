package com.minechinima.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TranslationBackpack {
    private final Map<Locale, TranslationList> bundles = new HashMap<>();
    // TODO dynamic adding of translations

    public void loadTranslations(@NotNull ResourceBundleProvider provider, @NotNull Locale locale) {
        assert provider != null;
        assert locale != null;

        Locale desiredLocale = locale;

        ResourceBundle bundle = provider.load(desiredLocale);
        assert bundle != null;

        TranslationList translationList = bundles.get(locale);
        if (translationList == null) {
            desiredLocale = locale.stripExtensions();
            translationList = bundles.get(desiredLocale);
        }
        if (translationList == null) {
            translationList = new TranslationList();
            bundles.put(desiredLocale, translationList);
        }

        translationList.addTranslations(bundle);
    }

    @Nullable
    public String getTranslation(@NotNull String key, @NotNull Locale locale) {
        assert key != null;
        assert locale != null;

        TranslationList translationList = bundles.get(locale);
        if (translationList == null) {
            translationList = bundles.get(locale.stripExtensions());
        }

        try {
            return translationList != null ? translationList.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
