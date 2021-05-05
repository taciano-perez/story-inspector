package com.o3.storyinspector.annotation.blocks;

import com.o3.storyinspector.annotation.util.StanfordCoreNLPUtils;
import com.o3.storyinspector.storydom.Block;

import java.util.List;

/**
 * Responsible for breaking down a block into sentence blocks.
 */
public class SentenceSplitter {

    public static List<String> splitSentences(final Block block) {
        return StanfordCoreNLPUtils.splitSentences(block.getBody());

    }
}