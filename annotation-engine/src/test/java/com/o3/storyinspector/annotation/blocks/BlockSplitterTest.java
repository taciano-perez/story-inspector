package com.o3.storyinspector.annotation.blocks;

import com.o3.storyinspector.annotation.AnnotationEngine;
import com.o3.storyinspector.annotation.util.FileUtils;
import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Chapter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockSplitterTest {

    private static final String SAMPLE_CHAPTER_PATH = BlockSplitterTest.class.getResource("/siddhartha-chapter1.txt").getPath();

    private static final String EXPECTED_FIRST_BLOCK_BODY = BlockSplitterTest.class.getResource("/siddhartha-block1.txt").getPath();

    private static final String LARGE_SENTENCE = "I'm not bored or anything but what I feel like doing right now is standing up, stepping into a lift, descending at high speed to ground level, tip-toeing through a fire door, pausing beside the Thames, crossing a footbridge, trudging through endless suburbs, walking up a hill, conceiving a dislike for Kent, sleeping in a wet haystack, waking up with a headache, cadging a cup of tea, scrambling over a ridge, spotting the sea, sniffing the salt air, loafing around a port, sneaking up a gang-plank, playing a slot machine, putting my face in the breeze, walking round the boat, eating a plate of fish, queuing up with the riff-raff, grazing my shoes, walking down the gangplank, getting a whiff of Gitanes, smiling at the roof tiles, entering a bar on the quayfront, ordering a cognac, watching a TV chat show, thinking about nothing, getting into a fight, falling off the bar stool, lying in the sawdust, crawling away, removing my jacket, eating a baguette au jambon, fancying a waitress called Francoise, persuading her to run away, listening to wind in the long grass, sleeping under a horse chestnut tree, reading Emile Zola, sweet-talking a farmer's wife, shooting her husband, scuttling off with a duck, cooking it with olives, swigging wine from a plastic bottle, sitting on an ants' nest, renaming the constellations, reviewing world history, mulling over the meaning of passeisme, taking off my tie, perspiring feverishly, composing an ode to a thunderstorm, drinking a toast to European unity, jumping into a lake, kissing Francoise with a rare passion, destabilising the bourgeoisie, pressing on to Orleans, climbing the cathedral spire, gasping at the blue world, wondering if it knew any end, climbing down the spire, scraping a hole in my trousers, thumping a policeman, being arrested, spending a night in a cell, jumping out through an open window, stealing an apricot patisserie, getting sticky fingers, asking Francoise to marry me, threatening a priest, converting to Catholicism, confessing my sins, holding my head in my hands, selling a pornographic painting, blundering into Paris, fuming at the driving, looking in a mirror, having a shave, finding work in a hotel, carrying people's suitcases, polishing their shoes, spending my savings, writing a treatise on the rights of man, evoking 1789, manning a barricade, getting the sack, setting up a vegetable stall by the Seine, buying a suit, moving to larger premises, opening a supermarket, taking over my rivals, monopolising the sale of food in France, buying Versailles, declaring war on the constitution, running for mayor, standing on a tax- cutting platform, losing narrowly, becoming disillusioned with my status, throwing in the towel, despairing of humanity, resuming my journey, marching into the sun, striding over Spain, reaching Africa, shacking up in Morocco, fathering a dozen children and dying of black-water fever.";

    @Test
    void splitChapter() throws IOException {
        // given
        final Block inputBlock = new Block();
        inputBlock.setBody(FileUtils.readStringFromUri(SAMPLE_CHAPTER_PATH));
        final Chapter inputChapter = new Chapter();
        inputChapter.setId("1");
        inputChapter.getBlocks().add(inputBlock);

        // when
        final List<Block> outputBlocks = BlockSplitter.splitChapter(inputChapter, AnnotationEngine.NR_WORDS_PER_BLOCK);

        // then
        assertEquals(11, outputBlocks.size());
        assertEquals("277", outputBlocks.get(0).getWordCount());
        assertEquals("1#1", outputBlocks.get(0).getId());
        assertEquals(FileUtils.readStringFromUri(EXPECTED_FIRST_BLOCK_BODY).trim(), outputBlocks.get(0).getBody());
    }

    @Test
    void splitChapterBlock_SmallerThanN() {
        // given
        final Block inputBlock = new Block();
        inputBlock.setBody("This is shorter than 250 words.");
        final Chapter inputChapter = new Chapter();
        inputChapter.setId("1");
        inputChapter.getBlocks().add(inputBlock);

        // when
        final List<Block> outputBlocks = BlockSplitter.splitChapter(inputChapter, AnnotationEngine.NR_WORDS_PER_BLOCK);

        // then
        assertEquals(1, outputBlocks.size());
        assertEquals("6", outputBlocks.get(0).getWordCount());
        assertEquals("1#1", outputBlocks.get(0).getId());
        assertEquals("This is shorter than 250 words.", outputBlocks.get(0).getBody());
    }

    @Test
    void splitChapterBlock_SmallSentenceLargeSentence() {
        // given
        final Block inputBlock = new Block();
        inputBlock.setBody("This is shorter than 250 words. However, the next sentence is extremely verbose, " +
                "as you can see for yourself: " + LARGE_SENTENCE);
        final Chapter inputChapter = new Chapter();
        inputChapter.setId("1");
        inputChapter.getBlocks().add(inputBlock);

        // when
        final List<Block> outputBlocks = BlockSplitter.splitChapter(inputChapter, AnnotationEngine.NR_WORDS_PER_BLOCK);

        // then
        assertEquals(2, outputBlocks.size());
        assertEquals("6", outputBlocks.get(0).getWordCount());
        assertEquals("1#1", outputBlocks.get(0).getId());
        assertEquals("This is shorter than 250 words.", outputBlocks.get(0).getBody());
    }

}