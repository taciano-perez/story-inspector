package com.o3.storyinspector.annotation.entities;

import com.o3.storyinspector.storydom.Character;

import java.util.Objects;

public class NECharacter {

    private String name;

    public NECharacter(String name) {
        this.name = name;
    }

    public Character asCharacter() {
        final Character character = new Character();
        character.setName(this.getName());
        return character;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NECharacter that = (NECharacter) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
