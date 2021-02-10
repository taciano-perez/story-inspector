package com.o3.storyinspector.annotation.entities;

import com.o3.storyinspector.storydom.Location;

import java.util.Objects;

/**
 * Named Entity - Location.
 */
public class NELocation {

    private String name;
    private String type;

    public NELocation(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Location asLocation() {
        final Location location = new Location();
        location.setName(this.getName());
        location.setType(this.getType());
        return location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NELocation that = (NELocation) o;
        return name.equals(that.name) &&
                type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

}
