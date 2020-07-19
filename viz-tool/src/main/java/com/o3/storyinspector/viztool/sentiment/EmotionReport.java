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

    private static final String CHART_FILE_SUFFIX = "-chart.jpg";

    private Book book;

    private String folderPath;

    private double maxEmotionScore;

    public EmotionReport(final Book book, final String folderPath) {
        this.book = book;
        this.folderPath = folderPath;
        this.maxEmotionScore = StoryDomUtils.getMaxEmotionScore(book);
    }

    public String asHtml() {
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
        final String filename = emotionType.asString() + CHART_FILE_SUFFIX;
        final EmotionCurveChart chart = new EmotionCurveChart(emotionType, book);
        try {
            chart.plotCurve(new File(folderPath + File.pathSeparator + filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "<span style=\"text-align: center;\"><img src=\"" + filename + "\" alt=\"" + emotionType.asString() + " Score Chart\"></span>";
    }

    private static String chapterTag(final Chapter chapter) {
        return "<h2 style=\"color:black;\">" + chapter.getTitle() + "</h2><br>\n<p>";
    }

    private String blockTags(final EmotionType emotionType, final Block block, final int blockId) {
        final Emotion emotion = StoryDomUtils.findEmotion(emotionType, block.getEmotions());
        if (emotion == null) return "";

        final double emotionScore = emotion.getScore().doubleValue();
        final double normalizedEmotionScore = emotionScore / maxEmotionScore;

        return "<span " +
                "title=\"" + normalizedEmotionScore * 100 + "%\"" +
                "style=\"background-color: " +
                SentimentColor.getSentimentColorCode(normalizedEmotionScore) +
                "\">" +
                " [#" + blockId + "] " +
                block.getBody() +
                "</span>\n";
    }

}
