package com.o3.storyinspector.annotation.readability;

import io.whelk.flesch.kincaid.ReadabilityCalculator;

public class FleschKincaidReadabilityInspector {

    public static double inspectFKGradeLevel(final String text) {
        // this FK lib does not like ellipses
        final String sanitizedText = text.replaceAll("\\.+", ".");
        return ReadabilityCalculator.calculateGradeLevel(sanitizedText);
    }

}
