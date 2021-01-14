package com.o3.storyinspector.annotation.util;

import java.util.Objects;

public class NamedEntityToken {

    private String type;

    private String name;

    public NamedEntityToken(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String typeAndName() {
        return this.getType() + ": " + this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedEntityToken that = (NamedEntityToken) o;
        return type.equals(that.type) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }

    @Override
    public String toString() {
        return "NamedEntityToken{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}