package com.o3.storyinspector.storydom.modifications;

import com.o3.storyinspector.storydom.*;
import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.constants.EntityType;
import com.o3.storyinspector.storydom.constants.ModificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookProcessorTest {

    private Book book;

    @BeforeEach
    void setUp() {
        // characters
        final Character sherlock = new Character();
        sherlock.setName("Sherlock Holmes");
        final Character watson = new Character();
        watson.setName("John Watson");
        final Character mary = new Character();
        mary.setName("Mary Watson");

        // locations
        final Location location1 = new Location();
        location1.setName("London");
        final Location location2 = new Location();
        location2.setName("Baker Street");

        // chapters
        final Chapter chapter1 = new Chapter();
        chapter1.setMetadata(new Metadata());
        chapter1.getMetadata().setCharacters(new Characters());
        chapter1.getMetadata().getCharacters().getCharacters().add(sherlock);
        chapter1.getMetadata().setLocations(new Locations());
        chapter1.getMetadata().getLocations().getLocations().add(location1);
        final Chapter chapter2 = new Chapter();
        chapter2.setMetadata(new Metadata());
        chapter2.getMetadata().setCharacters(new Characters());
        chapter2.getMetadata().getCharacters().getCharacters().add(watson);
        chapter2.getMetadata().getCharacters().getCharacters().add(mary);
        chapter2.getMetadata().setLocations(new Locations());
        chapter2.getMetadata().getLocations().getLocations().add(location2);

        // book
        book = new Book();
        book.getChapters().add(chapter1);
        book.getChapters().add(chapter2);
    }

    @Test
    void removeCharacter() {
        // given
        final Modification removeCharacter = new Modification();
        removeCharacter.setTransformation(ModificationType.REMOVE.asString());
        removeCharacter.setName("John Watson");
        removeCharacter.setEntity(EntityType.CHARACTER.asString());
        book.getModifications().add(removeCharacter);

        // when
        BookProcessor.applyModifications(book);

        // then
        assertEquals(1, book.getChapters().get(0).getMetadata().getCharacters().getCharacters().size());
        assertEquals(1, book.getChapters().get(1).getMetadata().getCharacters().getCharacters().size());
        assertEquals("Mary Watson", book.getChapters().get(1).getMetadata().getCharacters().getCharacters().get(0).getName());
    }

    @Test
    void removeLocation() {
        // given
        final Modification removeLocation = new Modification();
        removeLocation.setTransformation(ModificationType.REMOVE.asString());
        removeLocation.setName("London");
        removeLocation.setEntity(EntityType.LOCATION.asString());
        book.getModifications().add(removeLocation);

        // when
        BookProcessor.applyModifications(book);

        // then
        assertEquals(0, book.getChapters().get(0).getMetadata().getLocations().getLocations().size());
        assertEquals(1, book.getChapters().get(1).getMetadata().getLocations().getLocations().size());
    }

    @Test
    void renameCharacter() {
        // given
        final Modification renameCharacter = new Modification();
        renameCharacter.setTransformation(ModificationType.RENAME.asString());
        renameCharacter.setName("Sherlock Holmes");
        renameCharacter.setNewName("Sherlock Gomes");
        renameCharacter.setEntity(EntityType.CHARACTER.asString());
        book.getModifications().add(renameCharacter);

        // when
        BookProcessor.applyModifications(book);

        // then
        assertEquals(1, book.getChapters().get(0).getMetadata().getCharacters().getCharacters().size());
        assertEquals("Sherlock Gomes", book.getChapters().get(0).getMetadata().getCharacters().getCharacters().get(0).getName());
        assertEquals(2, book.getChapters().get(1).getMetadata().getCharacters().getCharacters().size());
    }


    @Test
    void renameLocation() {
        // given
        final Modification renameLocation = new Modification();
        renameLocation.setTransformation(ModificationType.RENAME.asString());
        renameLocation.setName("Baker Street");
        renameLocation.setNewName("Avenida Paulista");
        renameLocation.setEntity(EntityType.LOCATION.asString());
        book.getModifications().add(renameLocation);

        // when
        BookProcessor.applyModifications(book);

        // then
        assertEquals(1, book.getChapters().get(0).getMetadata().getLocations().getLocations().size());
        assertEquals(1, book.getChapters().get(1).getMetadata().getLocations().getLocations().size());
        assertEquals("Avenida Paulista", book.getChapters().get(1).getMetadata().getLocations().getLocations().get(0).getName());
    }

}