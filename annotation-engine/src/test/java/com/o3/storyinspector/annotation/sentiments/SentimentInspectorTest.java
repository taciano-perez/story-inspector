package com.o3.storyinspector.annotation.sentiments;

import com.o3.storyinspector.annotation.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SentimentInspectorTest {

    private static final String SAMPLE_CHAPTER_PATH = SentimentInspectorTest.class.getResource("/study-in-scarlet-chapter1.txt").getPath().replaceFirst("/", "");

    @Test
    void inspectSentimentScore() throws Exception {
        // given
        final String sampleChapter = FileUtils.readString(Paths.get(SAMPLE_CHAPTER_PATH));
        final int expectedSentiment = 1;

        // when
        final int sentimentScore = SentimentInspector.inspectSentimentScore(sampleChapter);

        // then
        assertEquals(expectedSentiment, sentimentScore);
    }
}