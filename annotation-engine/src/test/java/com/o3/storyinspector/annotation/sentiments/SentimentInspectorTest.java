package com.o3.storyinspector.annotation.sentiments;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class SentimentInspectorTest {

    private static final String SAMPLE_CHAPTER_PATH = SentimentInspectorTest.class.getResource("/study-in-scarlet-chapter1.txt").getPath().replaceFirst("/", "");

    @Test
    void inspectSentimentScore() throws Exception {
        // given
        final String sampleChapter = Files.readString(Paths.get(SAMPLE_CHAPTER_PATH));
        final int expectedSentiment = 1;

        // when
        final int sentimentScore = SentimentInspector.inspectSentimentScore(sampleChapter);

        // then
        assertEquals(expectedSentiment, sentimentScore);
    }
}