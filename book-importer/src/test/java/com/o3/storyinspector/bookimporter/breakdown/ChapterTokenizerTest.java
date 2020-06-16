package com.o3.storyinspector.bookimporter.breakdown;

import com.o3.storyinspector.storydom.Chapter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChapterTokenizerTest {

    private static final String SAMPLE_BOOK_PATH = ChapterTokenizerTest.class.getResource("/a-study-in-scarlett-244-0.txt").getPath().replaceFirst("/", "");

    @Test
    void tokenize() {
        List<Chapter> chapters = ChapterTokenizer.tokenize(SAMPLE_BOOK_PATH);
        assertEquals(14, chapters.size());
    }
}