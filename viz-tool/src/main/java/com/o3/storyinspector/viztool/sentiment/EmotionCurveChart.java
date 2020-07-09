package com.o3.storyinspector.viztool.sentiment;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.Emotion;
import com.o3.storyinspector.storydom.constants.EmotionType;
import com.o3.storyinspector.storydom.util.StoryDomUtils;
import com.o3.storyinspector.viztool.chart.CustomSymbolAxis;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class EmotionCurveChart {

    private static final int IMG_WIDTH = 1280;
    private static final int IMG_HEIGHT = 480;

    private static final int SERIES_EMOTION_SCORE = 0;
    private static final int SERIES_MOVING_AVERAGE = 1;

    private static final Map<Integer, String> Y_AXIS_LABELS = Map.of(0, "0%", 1, "100%");

    private EmotionType emotionType;

    private Book book;

    public EmotionCurveChart(final EmotionType emotionType, final Book book) {
        this.emotionType = emotionType;
        this.book = book;
    }

    public void plotCurve(final File lineChart) throws Exception {
        ChartUtils.saveChartAsJPEG(lineChart, createChart(1), IMG_WIDTH, IMG_HEIGHT);
    }

    private JFreeChart createChart(final double yAxisMaxBound) throws ParseException {
        // chart settings
        final NumberAxis xAxis = new NumberAxis("Storyline (250-word blocks)");
        final NumberAxis yAxis = new CustomSymbolAxis(emotionType.asString() + " score", Y_AXIS_LABELS);
        yAxis.setRange(0, yAxisMaxBound);

        // series 0: story score
        final XYSeries series = new XYSeries("Story");
        final Map<Integer, Double> emotionScore = getEmotionScores();
        emotionScore
                .forEach((blockId, sentiment) -> series.add(blockId, sentiment));
        xAxis.setRange(1, emotionScore.keySet().stream().mapToInt(v -> v).max().orElseThrow(NoSuchElementException::new));
        final XYDataset sentimentDataset = new XYSeriesCollection(series);
        final XYSplineRenderer renderer = createRenderer(Color.BLUE, new BasicStroke(3.0f));
        final XYPlot xyPlot = new XYPlot(sentimentDataset, xAxis, yAxis, renderer);
        xyPlot.setDataset(SERIES_EMOTION_SCORE, sentimentDataset);

        // series 1: moving average
        final XYDataset movingAverageDataSet = MovingAverage.createMovingAverage(sentimentDataset, " Moving Average", 16L, 0L);
        xyPlot.setDataset(SERIES_MOVING_AVERAGE, movingAverageDataSet);
        xyPlot.setRenderer(SERIES_MOVING_AVERAGE, createRenderer(Color.ORANGE, new BasicStroke(
                4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[]{6.0f, 6.0f}, 0.0f
        )));

        // draw chart
        return new JFreeChart(xyPlot);
    }

    private static XYSplineRenderer createRenderer(final Color color, final Stroke stroke) {
        final XYSplineRenderer renderer = new XYSplineRenderer(16);
        renderer.setAutoPopulateSeriesStroke(false);
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setSeriesStroke(SERIES_EMOTION_SCORE, stroke);
        renderer.setSeriesPaint(SERIES_EMOTION_SCORE, color);
        renderer.setSeriesShapesVisible(SERIES_EMOTION_SCORE, false);  // remove dots from lines
        return renderer;
    }

    private Map<Integer, Double> getEmotionScores() {
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
