package com.o3.storyinspector.gui.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {

    static I18N singleton = new I18N();

    Locale currentLocale;
    ResourceBundle messages;

    private I18N() {
        final String BUNDLE_FILENAME = "storyinspectori18n";
        currentLocale = new Locale("en", "US");
        messages = ResourceBundle.getBundle(BUNDLE_FILENAME, currentLocale);
    }

    public static I18N getInstance() {

        return singleton;
    }

    public static String stringFor(final String id) {
        return getInstance().messages.getString(id);
    }
}
