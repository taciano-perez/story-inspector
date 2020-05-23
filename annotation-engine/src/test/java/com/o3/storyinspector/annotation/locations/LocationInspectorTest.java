package com.o3.storyinspector.annotation.locations;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LocationInspectorTest {

    private static final String SAMPLE_CHAPTER_PATH = LocationInspectorTest.class.getResource("/study-in-scarlet-chapter1.txt").getPath().replaceFirst("/", "");

    @Test
    void inspectNamedLocations() throws Exception {
        // given
        final String sampleChapter = Files.readString(Paths.get(SAMPLE_CHAPTER_PATH));
        final Set<String> expectedLocations = Set.of("New", "Stamford", "Afghanistan", "Bombay", "Bradford", "London", "England", "Peshawar", "India");

        // when
        final Set<String> namedLocations = LocationInspector.inspectNamedLocations(sampleChapter);

        // then
        assertEquals(namedLocations, expectedLocations);
    }
}