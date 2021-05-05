package com.o3.storyinspector.annotation.blocks;

import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Chapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for breaking down a chapter into text blocks of N size.
 */
public class BlockSplitter {

    private static final Logger LOG = LoggerFactory.getLogger(BlockSplitter.class);

    /**
     * Given a chapter, breaks its text into blocks of approximately N wordsPerBlock
     * (avoiding cutting sentences in the middle).
     *
     * @param chapter       the chapter to be analyzed
     * @param wordsPerBlock desired number of N words per block
     * @return a list of new blocks with sentiment score and word count
     */
    public static List<Block> splitChapter(final Chapter chapter, final int wordsPerBlock) {
        final List<Block> newBlocks = new ArrayList<>();
        for (final Block oldBlock : chapter.getBlocks()) {
            final List<String> sentences = SentenceSplitter.splitSentences(oldBlock);
            int blockCounter = 1;
            BlockBuilder blockBuilder = new BlockBuilder(chapter, blockCounter++);
            for (final String sentence : sentences) {
                if (blockBuilder.getWordCount() + WordCountInspector.inspectWordCount(sentence) > wordsPerBlock * 1.5) {
                    // create blocks smaller than N if the next sentence is too long
                    newBlocks.add(blockBuilder.build());
                    blockBuilder = new BlockBuilder(chapter, blockCounter++);
                }
                blockBuilder.appendSentence(sentence);
                if (blockBuilder.getWordCount() >= wordsPerBlock) {
                    newBlocks.add(blockBuilder.build());
                    blockBuilder = new BlockBuilder(chapter, blockCounter++);
                }
            }
            if (blockBuilder.getCharacterCount() > 0) {
                // flush the last block
                newBlocks.add(blockBuilder.build());
            }
        }
        return newBlocks;
    }

}
