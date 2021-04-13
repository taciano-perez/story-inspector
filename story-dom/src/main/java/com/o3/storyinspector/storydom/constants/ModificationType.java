package com.o3.storyinspector.storydom.constants;

import java.security.InvalidParameterException;

public enum ModificationType {
    RENAME("rename"),
    REMOVE("remove");

    private String modificationString;

    ModificationType(final String modificationString) {
        this.modificationString = modificationString;
    }

    public String asString() {
        return modificationString;
    }

    public static ModificationType modificationTypeFor(final String name) {
        if (ModificationType.RENAME.asString().equals(name.toLowerCase())) {
            return ModificationType.RENAME;
        }
        if (ModificationType.REMOVE.asString().equals(name.toLowerCase())) {
            return ModificationType.REMOVE;
        }
        throw new InvalidParameterException("unknown modification type");
    }

}
