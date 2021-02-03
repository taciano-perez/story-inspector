package com.o3.storyinspector.annotation.wordcount;

import com.o3.storyinspector.annotation.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordCountInspectorTest {

    private static final String SAMPLE_CHAPTER_PATH = WordCountInspectorTest.class.getResource("/study-in-scarlet-chapter1.txt").getPath();

    @Test
    void inspectWordCount() throws IOException {
        // given
        final String sampleChapter = FileUtils.readStringFromUri(SAMPLE_CHAPTER_PATH);
        final int expectedWordCount = 2766;

        // when
        final int actualWordCount = WordCountInspector.inspectWordCount(sampleChapter);

        // then
        assertEquals(expectedWordCount, actualWordCount);
    }
}