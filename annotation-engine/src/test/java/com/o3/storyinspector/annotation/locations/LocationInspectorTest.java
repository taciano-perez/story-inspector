package com.o3.storyinspector.annotation.locations;

import com.o3.storyinspector.annotation.util.FileUtils;
import com.o3.storyinspector.storydom.Location;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationInspectorTest {

    private static final String SAMPLE_CHAPTER_PATH = LocationInspectorTest.class.getResource("/study-in-scarlet-chapter1.txt").getPath().replaceFirst("/", "");

    @Test
    void inspectNamedLocations() throws Exception {
        // given
        final String sampleChapter = FileUtils.readString(Paths.get(SAMPLE_CHAPTER_PATH));
        final SortedSet<String> sortedExpectedLocations = new TreeSet<>(
                Arrays.asList("CITY: Frankfort", "COUNTRY: England",
                        "COUNTRY: Afghanistan", "LOCATION: Candahar", "CITY: New Orleans", "CITY: Stamford",
                        "LOCATION: Fusiliers", "LOCATION: Holborn", "LOCATION: Berkshires", "CITY: London",
                        "LOCATION: Maiwand", "CITY: Bradford", "COUNTRY: India", "CITY: Baker", "CITY: Portsmouth",
                        "CITY: Bombay", "LOCATION: Netley", "CITY: Peshawar")
        );

        // when
        Set<Location> locations = LocationInspector.inspectNamedLocations(sampleChapter);
        final SortedSet<String> sortedNamedLocations = locations.stream()
                .map(l -> l.getType() + ": " + l.getName()).collect(Collectors.toCollection(TreeSet::new));

        // then
        assertEquals(sortedExpectedLocations, sortedNamedLocations);
    }
}