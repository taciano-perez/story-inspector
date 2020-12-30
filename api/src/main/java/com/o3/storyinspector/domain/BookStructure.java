package com.o3.storyinspector.domain;

import java.util.List;

/**
 * Domain object for book structure entities.
 */
public class BookStructure {

    private String title;
    private String author;
    private long wordcount;
    private List<Chapter> chapters;

    public BookStructure(String title, String author, long wordcount, List<Chapter> chapters) {
        this.title = title;
        this.author = author;
        this.wordcount = wordcount;
        this.chapters = chapters;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public long getWordcount() {
        return wordcount;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
}
