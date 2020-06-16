package com.o3.storyinspector.annotation.wordcount;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordCountInspectorTest {

    private static final String SAMPLE_CHAPTER_PATH = WordCountInspectorTest.class.getResource("/study-in-scarlet-chapter1.txt").getPath().replaceFirst("/", "");

    @Test
    void inspectWordCount() throws IOException {
        // given
        final String sampleChapter = Files.readString(Paths.get(SAMPLE_CHAPTER_PATH));
        final int expectedWordCount = 2766;

        // when
        final int actualWordCount = WordCountInspector.inspectWordCount(sampleChapter);

        // then
        assertEquals(expectedWordCount, actualWordCount);
    }
}