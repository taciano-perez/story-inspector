package com.o3.storyinspector.gui.core.domain;

import com.o3.storyinspector.storydom.Book;
import edu.stanford.nlp.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Characters {
    private String bookTitle;
    private List<Character> characters;
    private int totalNumOfChapters;

    public Characters(final String bookTitle, final List<Character> characters, final int totalNumOfChapters) {
        this.bookTitle = bookTitle;
        this.characters = characters;
        this.totalNumOfChapters = totalNumOfChapters;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public int getTotalNumOfChapters() {
        return totalNumOfChapters;
    }

    public static Characters buildFromBook(final Book book) {
        final int totalNumOfChapters = book.getChapters().size();
        final Map<String, Character> charactersByName = new ArrayMap<>();
        int chapterId = 1;
        for (final com.o3.storyinspector.storydom.Chapter chapter : book.getChapters()) {
            final Integer chapterIdInteger = chapterId; // copy id to make it final
            chapter.getMetadata().getCharacters().getCharacters()
                    .forEach(c -> addOrUpdateCharacterEntry(c.getName(), chapterIdInteger, totalNumOfChapters, charactersByName));
            chapterId++;
        }
        return new Characters(book.getTitle(), new ArrayList<>(charactersByName.values()), totalNumOfChapters);
    }

    private static void addOrUpdateCharacterEntry(final String name, final int chapterId, final int totalNumOfChapters, final Map<String, Character> charactersByName) {
        Character character = charactersByName.get(name);
        if (character == null) {
            character = new Character(name, new ArrayList<>(), 0.0);
            charactersByName.put(name, character);
        }
        character.getChapters().add(chapterId);
        character.increaseTotalPercentageOfChapters((double)1/totalNumOfChapters);
    }

}
