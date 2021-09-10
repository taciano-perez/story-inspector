package com.o3.storyinspector.gui.reportarea.emotion;

import com.o3.storyinspector.gui.core.domain.EmotionChartData;
import com.o3.storyinspector.gui.utils.StringFormatter;
import com.o3.storyinspector.gui.view.StackedAreaChartWithMarkers;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.constants.EmotionType;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EmotionChart<T1, T2> extends StackedAreaChartWithMarkers {

    static final Logger LOGGER = LoggerFactory.getLogger(EmotionChart.class);

    Book book;

    public static EmotionChart buildEmotionChart(final Book book, final EmotionType emotionType) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Paragraphs");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Emotion Score");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(1.0);
        yAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return (StringFormatter.formatInteger(number.doubleValue() * 100)) + "%";
            }

            @Override
            public Number fromString(String s) {
                return null;
            }
        });

        return new EmotionChart<String, Double>(xAxis, yAxis, book, emotionType);
    }

    public static EmotionChart buildSentimentChart(final Book book) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Paragraphs");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Sentiment Score");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(-1);
        yAxis.setUpperBound(1);
        yAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                if (number.intValue() == 0) {
                    return "Neutral";
                } else if (number.intValue() > 0) {
                    return "Positive";
                } else if (number.intValue() < 0) {
                    return "Negative";
                } else {
                    return "";
                }
            }

            @Override
            public Number fromString(String s) {
                return null;
            }
        });

        return new EmotionChart<String, Double>(xAxis, yAxis, book);
    }

    public EmotionChart(final Axis xAxis, final Axis yAxis, final Book book) {
        super(xAxis, yAxis);
        this.book = book;

        try {
            EmotionChartData emotionChartData = EmotionChartData.buildSentimentChartFromBook(book);
            final Series dataSeriesForEmotion = createDataSeriesForEmotion("Sentiment", emotionChartData);
            this.getData().add(dataSeriesForEmotion);

            embellishChart("Sentiment", emotionChartData);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
    }

    public EmotionChart(final Axis xAxis, final Axis yAxis, final Book book, final EmotionType emotionType) {
        super(xAxis, yAxis);
        this.book = book;

        try {
            final EmotionChartData emotionChartData = EmotionChartData.buildEmotionChartFromBook(book, emotionType);
            final Series dataSeriesForEmotion = createDataSeriesForEmotion(emotionType.asString(), emotionChartData);
            this.getData().add(dataSeriesForEmotion);

            embellishChart(emotionType.asString(), emotionChartData);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
    }

    private void embellishChart(final String seriesName, final EmotionChartData emotionChartData) {
        // show tooltip
        int blockNumber = 0;
        for (Object o : this.getData()) {
            XYChart.Series<String, Number> s = (XYChart.Series<String, Number>) o;
            for (XYChart.Data<String, Number> data : s.getData()) {
                final StackPane stackPane = (StackPane) data.getNode();
//                stackPane.setVisible(false); // hide symbol
                final String block = emotionChartData.getBlocks().get(blockNumber);
                final String percentage = seriesName + ": " + StringFormatter.formatPercentage(emotionChartData.getScores().get(blockNumber));
                final String chapterCaption = "Chapter " + EmotionChartData.getChapterNumber(blockNumber, emotionChartData.getChapterDividers());
                blockNumber++;

                final Tooltip tooltip = new Tooltip(chapterCaption + "\n"
                        + "Paragraph #" + blockNumber + "\n"
                        + percentage + "\n" + block);
                tooltip.setPrefWidth(500);
                tooltip.setWrapText(true);
                tooltip.setShowDelay(Duration.seconds(0));
                Tooltip.install(stackPane, tooltip);
            }
        }
        // show chapter lanes
        final List<Integer> chapterDividers = emotionChartData.getChapterDividers();
        for (int i=0; i<chapterDividers.size(); i++) {
            final int chapterDivider = chapterDividers.get(i);
            final String dividerLabel = emotionChartData.getLabels().get(chapterDivider);
            this.addVerticalValueMarker(new Data(dividerLabel, 0.0));
        }
        // adjust height
        this.setMinHeight(300);
    }

    private XYChart.Series createDataSeriesForEmotion(final String seriesName, final EmotionChartData emotionChartData) {
        final XYChart.Series dataSeries = new XYChart.Series();
        dataSeries.setName(seriesName);

        for (int i = 0; i < emotionChartData.getScores().size(); i++) {
            final String label = emotionChartData.getLabels().get(i);
            final double score = emotionChartData.getScores().get(i);
            dataSeries.getData().add(new Data<>(label, score));

        }

        return dataSeries;
    }

}
