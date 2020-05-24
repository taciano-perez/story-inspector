package com.o3.storyinspector.annotation.locations;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class LocationInspectorTest {

    private static final String SAMPLE_CHAPTER_PATH = LocationInspectorTest.class.getResource("/study-in-scarlet-chapter1.txt").getPath().replaceFirst("/", "");

    @Test
    void inspectNamedLocations() throws Exception {
        // given
        final String sampleChapter = Files.readString(Paths.get(SAMPLE_CHAPTER_PATH));
        final SortedSet<String> sortedExpectedLocations = new TreeSet<>(
                Set.of("CITY: Frankfort", "COUNTRY: England",
                        "COUNTRY: Afghanistan", "LOCATION: Candahar", "CITY: New Orleans", "CITY: Stamford",
                        "LOCATION: Fusiliers", "LOCATION: Holborn", "LOCATION: Berkshires", "CITY: London",
                        "LOCATION: Maiwand", "CITY: Bradford", "COUNTRY: India", "CITY: Baker", "CITY: Portsmouth",
                        "CITY: Bombay", "LOCATION: Netley", "CITY: Peshawar")
        );

        // when
        final SortedSet<String> sortedNamedLocations = new TreeSet<>(LocationInspector.inspectNamedLocations(sampleChapter));

        // then
        assertEquals(sortedExpectedLocations, sortedNamedLocations);
    }
}