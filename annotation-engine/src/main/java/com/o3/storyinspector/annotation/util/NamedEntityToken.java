package com.o3.storyinspector.annotation.util;

public record NamedEntityToken(String type, String name) {

    public String typeAndName() {
        return this.type() + ": " + this.name();
    }
}