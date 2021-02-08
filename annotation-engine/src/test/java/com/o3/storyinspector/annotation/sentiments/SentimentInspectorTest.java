package com.o3.storyinspector.annotation.sentiments;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.util.StoryDomUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SentimentInspectorTest {

    private static final String SENTENCE_POSITIVE = "I was very happy, it seemed that all good things in the world were benefiting me.";

    private static final String SENTENCE_NEUTRAL = "It was a regular day.";

    private static final String SENTENCE_NEGATIVE = "All those deaths were a tragedy beyond the most horrible nightmare.";

    private static final String BLOCK1 = "MR. SHERLOCK HOLMES .IN the year 1878 I took my degree of Doctor of Medicine of the University of London , and proceeded to Netley to go through the course prescribed for surgeons in the army .Having completed my studies there , I was duly attached to the Fifth Northumberland Fusiliers as Assistant Surgeon .The regiment was stationed in India at the time , and before I could join it , the second Afghan war had broken out .On landing at Bombay , I learned that my corps had advanced through the passes , and was already deep in the enemy ’s country .I followed , however , with many other officers who were in the same situation as myself , and succeeded in reaching Candahar in safety , where I found my regiment , and at once entered upon my new duties .The campaign brought honours and promotion to many , but for me it had nothing but misfortune and disaster .I was removed from my brigade and attached to the Berkshires , with whom I served at the fatal battle of Maiwand .There I was struck on the shoulder by a Jezail bullet , which shattered the bone and grazed the subclavian artery .I should have fallen into the hands of the murderous Ghazis had it not been for the devotion and courage shown by Murray , my orderly , who threw me across a pack - horse , and succeeded in bringing me safely to the British lines .";

    private static final String BLOCK2 = "Worn with pain , and weak from the prolonged hardships which I had undergone , I was removed , with a great train of wounded sufferers , to the base hospital at Peshawar .Here I rallied , and had already improved so far as to be able to walk about the wards , and even to bask a little upon the verandah , when I was struck down by enteric fever , that curse of our Indian possessions .For months my life was despaired of , and when at last I came to myself and became convalescent , I was so weak and emaciated that a medical board determined that not a day should be lost in sending me back to England .I was dispatched , accordingly , in the troopship “ Orontes , ” and landed a month later on Portsmouth jetty , with my health irretrievably ruined , but with permission from a paternal government to spend the next nine months in attempting to improve it .I had neither kith nor kin in England , and was therefore as free as air -- or as free as an income of eleven shillings and sixpence a day will permit a man to be .Under such circumstances , I naturally gravitated to London , that great cesspool into which all the loungers and idlers of the Empire are irresistibly drained .There I stayed for some time at a private hotel in the Strand , leading a comfortless , meaningless existence , and spending such money as I had , considerably more freely than I ought .";

    @Test
    void inspectSentimentScore_positive() {
        // given
        final double expectedSentiment = 1;

        // when
        final int sentimentScore = SentimentInspector.inspectSentimentScore(SENTENCE_POSITIVE);

        // then
        assertEquals(expectedSentiment, sentimentScore);
    }

    @Test
    void inspectSentimentScore_neutral() {
        // given
        final double expectedSentiment = 0;

        // when
        final int sentimentScore = SentimentInspector.inspectSentimentScore(SENTENCE_NEUTRAL);

        // then
        assertEquals(expectedSentiment, sentimentScore);
    }

    @Test
    void inspectSentimentScore_negative() {
        // given
        final double expectedSentiment = -1;

        // when
        final int sentimentScore = SentimentInspector.inspectSentimentScore(SENTENCE_NEGATIVE);

        // then
        assertEquals(expectedSentiment, sentimentScore);
    }

    @Test
    void inspectChapterSentimentScore_withBlock1() {
        // given
        final Block block = createBlock(BLOCK1, "252");
        final double expectedSentiment = -0.3024;
        final String expectedFormatttedSentimentScore = "-0,3024";

        // when
        final double sentimentScore = SentimentInspector.inspectSentimentScore(block, 250);
        final String formattedSentimentScore = StoryDomUtils.getFormatter().format(sentimentScore);

        // then
        assertEquals(expectedSentiment, sentimentScore);
        assertEquals(expectedFormatttedSentimentScore, formattedSentimentScore);
    }

    @Test
    void inspectChapterSentimentScore_withBlock2() {
        // given
        final Block block = createBlock(BLOCK2, "266");
        final double expectedSentiment = -0.456;

        // when
        final double sentimentScore = SentimentInspector.inspectSentimentScore(block, 250);

        // then
        assertEquals(expectedSentiment, sentimentScore);
    }

    private Block createBlock(final String body, final String wordCount) {
        final Block block = new Block();
        block.setBody(body);
        block.setWordCount(wordCount);
        return block;
    }

}