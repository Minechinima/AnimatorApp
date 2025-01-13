package com.minechinima.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.ResourceBundle;

public class TranslationList extends ListResourceBundle {
    private HashMap<String, String> translations = new HashMap<>();

    void addTranslations(@NotNull ResourceBundle bundle) {
        assert bundle != null;

        for (String key : bundle.keySet()) {
            if (translations.containsKey(key)) {
                continue;
            }

            translations.put(key, bundle.getString(key));
        }
    }

    @Override
    protected Object[][] getContents() {
        Object[][] contents = new Object[translations.size()][2];

        int i = 0;
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            contents[i][0] = entry.getKey();
            contents[i][1] = entry.getValue();

            i++;
        }

        return contents;
    }
}
