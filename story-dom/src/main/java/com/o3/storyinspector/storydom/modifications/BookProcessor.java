package com.o3.storyinspector.storydom.modifications;

import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.*;
import com.o3.storyinspector.storydom.constants.EntityType;
import com.o3.storyinspector.storydom.constants.ModificationType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Processor that knows how to apply modifications
 * (e.g., remove locations, rename characters, etc.)
 */
public class BookProcessor {

    public static Book applyModifications(final Book book) {
        for (Modification modification : book.getModifications()) {
            final EntityType entityType = EntityType.entityTypeFor(modification.getEntity());
            final ModificationType modificationType = ModificationType.modificationTypeFor(modification.getTransformation());
            for (final Chapter chapter : book.getChapters()) {
                if (modificationType == ModificationType.REMOVE) {
                    if (entityType == EntityType.CHARACTER) {
                        removeCharacterFromChapter(chapter, modification.getName());
                    } else if (entityType == EntityType.LOCATION) {
                        removeLocationFromChapter(chapter, modification.getName());
                    }
                } else if (modificationType == ModificationType.RENAME) {
                    if (entityType == EntityType.CHARACTER) {
                        renameCharacterOnChapter(chapter, modification.getName(), modification.getNewName());
                    } else if (entityType == EntityType.LOCATION) {
                        renameLocationOnChapter(chapter, modification.getName(), modification.getNewName());
                    }
                }
            }
        }
        return book;
    }

    private static void renameCharacterOnChapter(final Chapter chapter, final String characterName, final String newCharacterName) {
        final List<Character> chapterCharacters = chapter.getMetadata().getCharacters().getCharacters();
        final List<Character> charactersToAdd = new ArrayList<>();
        for (Iterator<Character> iterator = chapterCharacters.iterator(); iterator.hasNext();) {
            final Character character = iterator.next();
            if (character.getName() != null && character.getName().equals(characterName)) {
                iterator.remove();
                final Optional<Character> optionalExistingCharacter = chapterCharacters.stream().filter(c -> newCharacterName.equals(c.getName())).findFirst();
                if (!optionalExistingCharacter.isPresent()) {
                    final Character newCharacter = new Character();
                    newCharacter.setName(newCharacterName);
                    charactersToAdd.add(newCharacter);
                } else {
                    // if character is already present, we don't need to add it
                }
            }
        }
        chapterCharacters.addAll(charactersToAdd);
    }

    private static void renameLocationOnChapter(final Chapter chapter, final String locationName, final String newLocationName) {
        final List<Location> chapterLocations = chapter.getMetadata().getLocations().getLocations();
        final List<Location> locationsToAdd = new ArrayList<>();
        for (Iterator<Location> iterator = chapterLocations.iterator(); iterator.hasNext();) {
            final Location location = iterator.next();
            if (location.getName() != null && location.getName().equals(locationName)) {
                iterator.remove();
                final Optional<Location> optionalExistingLocation = chapterLocations.stream().filter(l -> newLocationName.equals(l.getName())).findFirst();
                if (!optionalExistingLocation.isPresent()) {
                    final Location newLocation = new Location();
                    newLocation.setName(newLocationName);
                    locationsToAdd.add(newLocation);
                } else {
                    // if location is already present, we don't need to add it
                }
            }
        }
        chapterLocations.addAll(locationsToAdd);
    }

    private static void removeCharacterFromChapter(final Chapter chapter, final String characterName) {
        chapter.getMetadata().getCharacters().getCharacters().removeIf(character -> character.getName() != null && character.getName().equals(characterName));
    }

    private static void removeLocationFromChapter(final Chapter chapter, final String locationName) {
        chapter.getMetadata().getLocations().getLocations().removeIf(location -> location.getName() != null && location.getName().equals(locationName));
    }

}
