package com.o3.storyinspector.domain;

import com.o3.storyinspector.storydom.constants.EmotionType;

import java.util.List;

/**
 * Domain object for chapter entities.
 */
public class Chapter {

    private long id;
    private String title;
    private long wordcount;
    private Double fkGrade;
    private List<String> Characters;
    private List<String> Locations;
    private List<EmotionType> dominantEmotions;

    public Chapter(final long id, final String title, final long wordcount, final Double fkGrade, final List<String> characters, final List<String> locations, final List<EmotionType> dominantEmotions) {
        this.id = id;
        this.title = title;
        this.wordcount = wordcount;
        this.fkGrade = fkGrade;
        this.Characters = characters;
        this.Locations = locations;
        this.dominantEmotions = dominantEmotions;
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

    public double getFkGrade() {
        return fkGrade;
    }

    public List<String> getCharacters() {
        return Characters;
    }

    public List<String> getLocations() {
        return Locations;
    }

    public List<EmotionType> getDominantEmotions() {
        return dominantEmotions;
    }
}
