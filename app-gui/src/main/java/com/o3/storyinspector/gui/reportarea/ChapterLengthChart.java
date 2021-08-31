package com.o3.storyinspector.gui.reportarea;

import com.o3.storyinspector.storydom.Book;
import javafx.scene.chart.*;

public class ChapterLengthChart extends BarChart {

    static int TRIM_LEN = 20;

    public static ChapterLengthChart build(final Book book) {
        // chapters axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelRotation(-45);
        // number of words axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("word count");

        final ChapterLengthChart chart = new ChapterLengthChart(xAxis, yAxis);

        // add data
        XYChart.Series dataSeries1 = new XYChart.Series();
        dataSeries1.setName(book.getTitle());
        book.getChapters().stream().forEach(chapter -> {
            final Data data = new XYChart.Data<>(trimText(chapter.getTitle()),
                    Integer.valueOf(chapter.getMetadata().getWordCount()));
            dataSeries1.getData().add(data);
        });
        chart.getData().add(dataSeries1);

        // formatting
        chart.setLegend(null);
        chart.setMinHeight(300);
        chart.setMinWidth(35 * book.getChapters().size());

        return chart;
    }

    private static String trimText(final String text) {
        if (text == null || text.length() < TRIM_LEN) return text;
        return text.substring(0, TRIM_LEN) + "...";
    }

    private ChapterLengthChart(Axis axis, Axis axis1) {
        super(axis, axis1);
    }
}
