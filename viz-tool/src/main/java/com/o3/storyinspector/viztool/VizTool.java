package com.o3.storyinspector.viztool;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.io.XmlReader;
import com.o3.storyinspector.viztool.sentiment.EmotionReport;
import com.o3.storyinspector.viztool.sentiment.SentimentColor;
import com.o3.storyinspector.viztool.sentiment.SentimentCurveChart;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;

import static com.o3.storyinspector.viztool.sentiment.SentimentCurveChart.CHART_FILE_NAME;

public class VizTool {

    private static final NumberFormat FORMATTER = NumberFormat.getInstance(Locale.FRANCE);

    public static void storyDomToHtml(final String inputBookPath, final String outputHtmlPath) throws Exception {
        final Book book = XmlReader.readBookFromXmlFile(inputBookPath);
        final PrintWriter printWriter = new PrintWriter(new FileWriter(outputHtmlPath));
        printWriter.write(bookToHtml(book));
        final String chartFilePath = new File(outputHtmlPath).getParentFile().getAbsolutePath();
        storyDomToSentimentChart(inputBookPath, chartFilePath);
        printWriter.close();
    }

    private static void storyDomToSentimentChart(final String inputBookPath, final String chartFilePath) throws Exception {
        final Book book = XmlReader.readBookFromXmlFile(inputBookPath);
        final File chartFile = new File(chartFilePath + CHART_FILE_NAME);
        SentimentCurveChart.plotSentimentCurve(book, chartFile);
    }

    public static String bookToHtml(final Book book) throws Exception {
        final StringBuilder builder = new StringBuilder();

        builder.append(headerTags());
        builder.append(bookTitleTag(book));
        builder.append(chartLink());
        int blockCounter = 1;
        for (final Chapter chapter : book.getChapters()) {
            builder.append(chapterTag(chapter));
            for (final Block block : chapter.getBlocks()) {
                builder.append(blockTags(block, blockCounter++));
            }
        }
        builder.append((new EmotionReport(book)).asHtml());
        builder.append(footerTags());

        return builder.toString();
    }

    private static String bookTitleTag(final Book book) {
        return "<h1 style=\"color:black;\">" + "Emotional Arc Report: " + book.getTitle() + "</h1><br>\n";
    }

    private static String chapterTag(final Chapter chapter) {
        return "<h2 style=\"color:black;\">" + chapter.getTitle() + "</h2><br>\n<p>";
    }

    private static String blockTags(final Block block, final int blockId) throws Exception {
        final double sentimentScore = FORMATTER.parse(block.getSentimentScore()).doubleValue();

        return "<span " +
                "title=\"" + sentimentScore * 100 + "%\"" +
                "style=\"background-color: " +
                SentimentColor.getSentimentColorCode(sentimentScore) +
                "\">" +
                " [#" + blockId + "] " +
                block.getBody() +
                "</span>\n";
    }

    private static String chartLink() {
        return "<span style=\"text-align: center;\"><img src=\"" + CHART_FILE_NAME + "\" alt=\"Sentiment Score Chart\"></span>";
    }

    private static String headerTags() {
        return """
                <html>
                <body style="background-color:white;">
                """;
    }

    private static String footerTags() {
        return """
                </body>
                </html>
                """;
    }

}