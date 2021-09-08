package com.o3.storyinspector.gui.core.domain;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.Emotion;
import com.o3.storyinspector.storydom.constants.EmotionType;
import com.o3.storyinspector.storydom.util.StoryDomUtils;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain object for chart entities.
 */
public class EmotionChartData {

    private String bookTitle;
    private String bookAuthor;
    private List<String> labels;
    private List<String> blocks;
    private List<Double> scores;
    private List<Integer> chapterDividers;

    public EmotionChartData(final String bookTitle, final String bookAuthor, final List<String> labels, final List<String> blocks, final List<Double> scores, final List<Integer> chapterDividers) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.labels = labels;
        this.blocks = blocks;
        this.scores = scores;
        this.chapterDividers = chapterDividers;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
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

    public List<Integer> getChapterDividers() {
        return chapterDividers;
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

    public static EmotionChartData buildSentimentChartFromBook(final Book book) throws JAXBException {
        final List<String> labels = new ArrayList<>();
        final List<String> blocks = new ArrayList<>();
        final List<Double> scores = new ArrayList<>();
        final List<Integer> chapterDividers = new ArrayList<>();
        int counter = 0;
        for (final Chapter chapter : book.getChapters()) {
            for (final Block block : chapter.getBlocks()) {
                counter++;
                final double sentimentScore = block.getSentimentScore().doubleValue();
                labels.add("#" + counter);
                blocks.add(block.getBody());
                scores.add(sentimentScore);
            }
            chapterDividers.add(counter);
        }
        chapterDividers.remove(chapterDividers.size()-1);  // remove last marker
        return new EmotionChartData(book.getTitle(), book.getAuthor(), labels, blocks, scores, chapterDividers);
    }

    public static EmotionChartData buildEmotionChartFromBook(final Book book, final EmotionType emotionType) throws JAXBException {
        final double maxEmotionScore = StoryDomUtils.getMaxEmotionScore(book);
        final List<String> labels = new ArrayList<>();
        final List<String> blocks = new ArrayList<>();
        final List<Double> scores = new ArrayList<>();
        final List<Integer> chapterDividers = new ArrayList<>();
        int counter = 0;
        for (final Chapter chapter : book.getChapters()) {
            for (final Block block : chapter.getBlocks()) {
                counter++;
                final Emotion emotion = StoryDomUtils.findEmotion(emotionType, block.getEmotions());
                final double emotionScore = emotion.getScore().doubleValue();
                final double normalizedEmotionScore = emotionScore / maxEmotionScore;
                labels.add("#" + counter);
                blocks.add(block.getBody());
                scores.add(normalizedEmotionScore);
            }
            chapterDividers.add(counter);
        }
        chapterDividers.remove(chapterDividers.size()-1);  // remove last marker
        return new EmotionChartData(book.getTitle(), book.getAuthor(), labels, blocks, scores, chapterDividers);
    }

}
