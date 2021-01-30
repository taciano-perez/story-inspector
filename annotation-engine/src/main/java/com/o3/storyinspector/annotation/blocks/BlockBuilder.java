package com.o3.storyinspector.annotation.blocks;

import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Chapter;

/**
 * Convenience class to build a new block incrementally.
 */
class BlockBuilder {

    private int blockId;
    private Chapter chapter;
    private StringBuilder stringBuilder = new StringBuilder();
    private int blockWordCount = 0;

    public BlockBuilder(final Chapter chapter, final int blockId) {
        this.chapter = chapter;
        this.blockId = blockId;
    }

    public void appendSentence(final String sentence) {
        if (this.getCharacterCount() > 0) {
            // add space between sentences
            this.stringBuilder.append(" ");
        }
        this.stringBuilder.append(sentence);
        this.blockWordCount += WordCountInspector.inspectWordCount(sentence);
    }

    public String getBody() {
        return this.stringBuilder.toString();
    }

    public int getWordCount() {
        return this.blockWordCount;
    }

    public int getCharacterCount() {
        return this.stringBuilder.length();
    }

    public Block build() {
        final Block block = new Block();
        block.setId(chapter.getId() + "#" + this.blockId);
        block.setBody(this.getBody());
        block.setWordCount(Integer.toString(this.getWordCount()));
        return block;
    }

}
