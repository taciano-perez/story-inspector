package com.o3.storyinspector.annotation.locations;

import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.Location;

import java.util.Set;

/**
 * Set of named entities extracted from a text, e.g., locations & characters.
 */
public class NamedEntities {
    private Set<Location> locations;
    private Set<Character> characters;

    public NamedEntities(Set<Location> locations, Set<Character> characters) {
        this.locations = locations;
        this.characters = characters;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public Set<Character> getCharacters() {
        return characters;
    }
}
