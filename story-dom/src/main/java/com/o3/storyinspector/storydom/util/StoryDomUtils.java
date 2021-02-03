package com.o3.storyinspector.storydom.util;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.Emotion;
import com.o3.storyinspector.storydom.constants.EmotionType;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public final class StoryDomUtils {

    public static final NumberFormat FORMATTER = NumberFormat.getInstance(Locale.FRANCE);

    /**
     * Returns the max emotion score of a book (among all emotions).
     *
     * @param book the input book
     * @return the max emotion score
     */
    public static double getMaxEmotionScore(final Book book) {
        double max = 0.0d;
        for (final Chapter chapter : book.getChapters()) {
            for (final Block block : chapter.getBlocks()) {
                for (final Emotion emotion : block.getEmotions()) {
                    final double score = emotion.getScore().doubleValue();
                    if (score > max) {
                        max = score;
                    }
                }
            }
        }
        return max;
    }

    /**
     * Finds an emotion type in an emotions list.
     *
     * @param emotionType  the emotion type
     * @param emotionsList the emotions list
     * @return the desired emotion type or null.
     */
    public static Emotion findEmotion(final EmotionType emotionType, final List<Emotion> emotionsList) {
        return emotionsList.stream()
                .filter(e -> emotionType.asString().equals(e.getType()))
                .findFirst()
                .orElse(null);
    }

}
