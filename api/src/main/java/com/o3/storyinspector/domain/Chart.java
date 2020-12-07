package com.o3.storyinspector.domain;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.Emotion;
import com.o3.storyinspector.storydom.constants.EmotionType;
import com.o3.storyinspector.storydom.util.StoryDomUtils;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Domain object for chart entities.
 */
public class Chart {

    private static final NumberFormat FORMATTER = NumberFormat.getInstance(Locale.FRANCE);

    private List<String> labels;
    private List<String> blocks;
    private List<Double> scores;

    public Chart(final List<String> labels, final List<String> blocks, final List<Double> scores) {
        this.labels = labels;
        this.blocks = blocks;
        this.scores = scores;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public List<Double> getScores() {
        return scores;
    }

    /**
     * Emotion score of a certain emotion type for a given book.
     *
     * @param book        the book
     * @param emotionType the emotion type
     * @return the emotion score
     */
    public static Map<Integer, Double> getEmotionScores(final Book book, final EmotionType emotionType) {
        final double maxEmotionScore = StoryDomUtils.getMaxEmotionScore(book);
        final Map<Integer, Double> scoresByBlock = new HashMap<>();
        int counter = 1;
        for (final Chapter chapter : book.getChapters()) {
            for (final Block block : chapter.getBlocks()) {
                final Emotion emotion = StoryDomUtils.findEmotion(emotionType, block.getEmotions());
                final double emotionScore = emotion.getScore().doubleValue();
                final double normalizedEmotionScore = emotionScore / maxEmotionScore;
                scoresByBlock.put(counter++, normalizedEmotionScore);
            }
        }
        return scoresByBlock;
    }

}
