package com.o3.storyinspector.bookimporter.breakdown;

import com.o3.storyinspector.storydom.Chapter;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChapterTokenizerTest {

    private static final String SAMPLE_BOOK_PATH = ChapterTokenizerTest.class.getResource("/a-study-in-scarlett-244-0.txt").getPath().replaceFirst("/", "");

    private static final String INPUT_PLAINTEXT_BOOK = """
            Chapter 1 A Startling Start.
            This is an example chapter wherein wondrous things would be expected by its eager author.
                        
            Chapter 2 The Unexciting Aftermath.
            This is another example chapter, but the action seems to unfold slower than expected. 
            """;

    @Test
    void tokenizeFromFile() {
        List<Chapter> chapters = ChapterTokenizer.tokenizeFromFile(SAMPLE_BOOK_PATH);
        assertEquals(14, chapters.size());
    }

    @Test
    void tokenizeFromReader() {
        List<Chapter> chapters = ChapterTokenizer.tokenizeFromReader(new StringReader(INPUT_PLAINTEXT_BOOK));
        assertEquals("Chapter 1", chapters.get(0).getTitle());
        assertEquals("This is an example chapter wherein wondrous things would be expected by its eager author .", chapters.get(0).getBlocks().get(0).getBody());
        assertEquals("Chapter 2", chapters.get(1).getTitle());
        assertEquals("This is another example chapter , but the action seems to unfold slower than expected .", chapters.get(1).getBlocks().get(0).getBody());
    }

}