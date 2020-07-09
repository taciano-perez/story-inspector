package com.o3.storyinspector.viztool.sentiment;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.Emotion;
import com.o3.storyinspector.storydom.constants.EmotionType;
import com.o3.storyinspector.storydom.util.StoryDomUtils;

import java.io.File;

/**
 * Prints a report about a book's emotional arc
 * based on the annotated book.
 */
public class EmotionReport {

    private static final String CHART_FILE_PREFIX = "./target/test-classes/";
    private static final String CHART_FILE_SUFFIX = "-chart.jpg";

    private Book book;

    private double maxEmotionScore = 0.0;

    public EmotionReport(final Book book) {
        this.book = book;
        this.maxEmotionScore = StoryDomUtils.getMaxEmotionScore(book);
    }

    public String asHtml() throws Exception {
        final StringBuilder builder = new StringBuilder();

        for (EmotionType emotionType : EmotionType.values()) {
            builder.append(reportTitleTag(emotionType));
            builder.append(chartLink(emotionType));
            int blockCounter = 1;
            for (final Chapter chapter : book.getChapters()) {
                builder.append(chapterTag(chapter));
                for (final Block block : chapter.getBlocks()) {
                    builder.append(blockTags(emotionType, block, blockCounter++));
                }
            }
        }
        return builder.toString();
    }

    private String reportTitleTag(final EmotionType emotionType) {
        return "<h1 style=\"color:black;\">" + "Emotion: " + emotionType + "</h1><br>\n";
    }

    private String chartLink(final EmotionType emotionType) {
        final String filePath = emotionType.asString() + CHART_FILE_SUFFIX;
        final EmotionCurveChart chart = new EmotionCurveChart(emotionType, book);
        try {
            chart.plotCurve(new File(CHART_FILE_PREFIX + filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "<span style=\"text-align: center;\"><img src=\"" + filePath + "\" alt=\"" + emotionType.asString() + " Score Chart\"></span>";
    }

    private static String chapterTag(final Chapter chapter) {
        return "<h2 style=\"color:black;\">" + chapter.getTitle() + "</h2><br>\n<p>";
    }

    private String blockTags(final EmotionType emotionType, final Block block, final int blockId) throws Exception {
        final Emotion emotion = StoryDomUtils.findEmotion(emotionType, block.getEmotions());
        if (emotion == null) return "";

        final double emotionScore = emotion.getScore().doubleValue();
        final double normalizedEmotionScore = emotionScore / maxEmotionScore;

        final StringBuilder builder = new StringBuilder();
        builder.append("<span ");
        builder.append("title=\"").append(normalizedEmotionScore * 100).append("%\"");
        builder.append("style=\"background-color: ");
        builder.append(SentimentColor.getSentimentColorCode(normalizedEmotionScore));
        builder.append("\">");
        builder.append(" [#").append(blockId).append("] ");
        builder.append(block.getBody());
        builder.append("</span>\n");
        return builder.toString();
    }

}
