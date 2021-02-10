package com.o3.storyinspector.annotation.entities;

import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.Location;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Set of named entities extracted from a text, e.g., locations & characters.
 */
public class NamedEntities {
    private Set<NELocation> locations;
    private Set<NECharacter> characters;

    public NamedEntities(Set<NELocation> locations, Set<NECharacter> characters) {
        this.locations = locations;
        this.characters = characters;
    }

    public Set<NELocation> getNELocations() {
        return locations;
    }

    public Set<NECharacter> getNECharacters() {
        return characters;
    }

    public Set<Location> getLocations() {
        return this.getNELocations().stream()
                .map(NELocation::asLocation)
                .collect(Collectors.toSet());
    }

    public Set<Character> getCharacters() {
        return this.getNECharacters().stream()
                .map(NECharacter::asCharacter)
                .collect(Collectors.toSet());
    }

    public void addAll(final NamedEntities other) {
        this.getNECharacters().addAll(other.getNECharacters());
        this.getNELocations().addAll(other.getNELocations());
    }
}
