package com.o3.storyinspector.domain;

import java.util.List;

/**
 * Domain object for chapter entities.
 */
public class Chapter {

    private long id;
    private String title;
    private long wordcount;
    private List<String> Characters;
    private List<String> Locations;

    public Chapter(final long id, final String title, final long wordcount, final List<String> characters, final List<String> locations) {
        this.id = id;
        this.title = title;
        this.wordcount = wordcount;
        this.Characters = characters;
        this.Locations = locations;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getWordcount() {
        return wordcount;
    }

    public List<String> getCharacters() {
        return Characters;
    }

    public List<String> getLocations() {
        return Locations;
    }

}
