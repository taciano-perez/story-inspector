package com.o3.storyinspector.domain;

import com.o3.storyinspector.storydom.Book;
import edu.stanford.nlp.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Locations {
    private String bookTitle;
    private List<Location> Locations;
    private int totalNumOfChapters;

    public Locations(final String bookTitle, final List<Location> Locations, final int totalNumOfChapters) {
        this.bookTitle = bookTitle;
        this.Locations = Locations;
        this.totalNumOfChapters = totalNumOfChapters;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public List<Location> getLocations() {
        return Locations;
    }

    public int getTotalNumOfChapters() {
        return totalNumOfChapters;
    }

    public static Locations buildFromBook(final Book book) {
        final int totalNumOfChapters = book.getChapters().size();
        final Map<String,Location> LocationsByName = new ArrayMap<>();
        int chapterId = 1;
        for (final com.o3.storyinspector.storydom.Chapter chapter : book.getChapters()) {
            final Integer chapterIdInteger = chapterId; // copy id to make it final
            chapter.getMetadata().getLocations().getLocations().stream()
                    .forEach(c -> addOrUpdateLocationEntry(c.getName(), chapterIdInteger, totalNumOfChapters, LocationsByName));
            chapterId++;
        }
        return new Locations(book.getTitle(), new ArrayList<>(LocationsByName.values()), totalNumOfChapters);
    }

    private static void addOrUpdateLocationEntry(final String name, final int chapterId, final int totalNumOfChapters, final Map<String,Location> LocationsByName) {
        Location Location = LocationsByName.get(name);
        if (Location == null) {
            Location = new Location(name, new ArrayList<>(), 0.0);
            LocationsByName.put(name, Location);
        }
        Location.getChapters().add(chapterId);
        Location.increaseTotalPercentageOfChapters((double)1/totalNumOfChapters);
    }

}
