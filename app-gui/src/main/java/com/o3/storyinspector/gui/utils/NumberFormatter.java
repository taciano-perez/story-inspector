package com.o3.storyinspector.gui.utils;

import java.text.DecimalFormat;

public class NumberFormatter {

    static String FORMAT_NUMBER_INTEGER = "numberInteger";

    public static String formatInteger(final double value) {
        final DecimalFormat df = new DecimalFormat(I18N.stringFor(FORMAT_NUMBER_INTEGER));
        return df.format(value);
    }

    public static String formatInteger(final String value) {
        return formatInteger(Integer.valueOf(value));
    }
}
