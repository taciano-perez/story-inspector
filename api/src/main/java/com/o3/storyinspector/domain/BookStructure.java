package com.o3.storyinspector.domain;

import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.Location;
import com.o3.storyinspector.storydom.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static BookStructure buildFromBook(final Book book) {
        final List<Chapter> chapterList = new ArrayList<>();
        long bookWordcount = 0;
        long id = 1;
        for (final com.o3.storyinspector.storydom.Chapter chapter : book.getChapters()) {
            final Metadata chapterMetadata = chapter.getMetadata();
            final long chapterWordcount = Long.parseLong(chapterMetadata.getWordCount());
            bookWordcount += chapterWordcount;
            chapterList.add(new Chapter(id++,
                    chapter.getTitle(),
                    chapterWordcount,
                    chapterMetadata.getCharacters().getCharacters().stream().map(Character::getName).collect(Collectors.toList()),
                    chapterMetadata.getLocations().getLocations().stream().map(Location::getName).collect(Collectors.toList())));
        }
        return new BookStructure(
                book.getTitle(),
                book.getAuthor(),
                bookWordcount,
                chapterList
        );
    }
}
