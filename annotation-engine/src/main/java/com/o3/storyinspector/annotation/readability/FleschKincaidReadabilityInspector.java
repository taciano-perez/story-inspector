package com.o3.storyinspector.annotation.readability;

import io.whelk.flesch.kincaid.ReadabilityCalculator;

public class FleschKincaidReadabilityInspector {

    public static double inspectFKGradeLevel(final String text) {
        return ReadabilityCalculator.calculateGradeLevel(text);
    }

}
