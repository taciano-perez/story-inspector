package com.o3.storyinspector.gui.utils;

import java.text.DecimalFormat;

public class StringFormatter {

    static String FORMAT_NUMBER_INTEGER = "numberInteger";
    static String FORMAT_NUMBER_PERCENT = "numberPercent";

    public static String formatInteger(final double value) {
        final DecimalFormat df = new DecimalFormat(I18N.stringFor(FORMAT_NUMBER_INTEGER));
        return df.format(value);
    }

    public static String formatInteger(final String value) {
        return formatInteger(Integer.valueOf(value));
    }

    public static String formatPercentage(final double value) {
        final DecimalFormat df = new DecimalFormat(I18N.stringFor(FORMAT_NUMBER_PERCENT));
        return df.format(value * 100);
    }

    public static String trimText(final String text, final int trimLen) {
        if (text == null || text.length() < trimLen) return text;
        return text.substring(0, trimLen) + "...";
    }

}
