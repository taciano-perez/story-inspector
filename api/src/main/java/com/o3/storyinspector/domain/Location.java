package com.o3.storyinspector.domain;

import java.util.List;

/**
 * Domain object for a book Location.
 */
public class Location {

    private String name;
    private List<Integer> chapters;
    private double totalPercentageOfChapters;

    public Location(final String name, final List<Integer> chapters, final double totalPercentageOfChapters) {
        this.name = name;
        this.chapters = chapters;
        this.totalPercentageOfChapters = totalPercentageOfChapters;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getChapters() {
        return chapters;
    }

    public double getTotalPercentageOfChapters() {
        return totalPercentageOfChapters;
    }

    public double increaseTotalPercentageOfChapters(final double delta) {
        this.totalPercentageOfChapters += delta;
        return this.totalPercentageOfChapters;
    }
}
