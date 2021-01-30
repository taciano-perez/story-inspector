package com.o3.storyinspector.annotation.blocks;

import com.o3.storyinspector.annotation.AnnotationEngine;
import com.o3.storyinspector.annotation.util.FileUtils;
import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Chapter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockSplitterTest {

    private static final String SAMPLE_CHAPTER_PATH = BlockSplitterTest.class.getResource("/siddhartha-chapter1.txt").getPath().replaceFirst("/", "");

    private static final String EXPECTED_FIRST_BLOCK_BODY = BlockSplitterTest.class.getResource("/siddhartha-block1.txt").getPath().replaceFirst("/", "");

    @Test
    void splitChapter() throws IOException {
        // given
        final Block inputBlock = new Block();
        inputBlock.setBody(FileUtils.readString(Paths.get(SAMPLE_CHAPTER_PATH)));
        final Chapter inputChapter = new Chapter();
        inputChapter.setId("1");
        inputChapter.getBlocks().add(inputBlock);

        // when
        final List<Block> outputBlocks = BlockSplitter.splitChapter(inputChapter, AnnotationEngine.NR_WORDS_PER_BLOCK);

        // then
        assertEquals(11, outputBlocks.size());
        assertEquals("277", outputBlocks.get(0).getWordCount());
        assertEquals("1#1", outputBlocks.get(0).getId());
        assertEquals(FileUtils.readString(Paths.get(EXPECTED_FIRST_BLOCK_BODY)).trim(), outputBlocks.get(0).getBody());
    }

}