package com.o3.storyinspector.gui.reportarea.structure;

import com.o3.storyinspector.gui.utils.StringFormatter;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

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
            final Data data = new XYChart.Data<>(StringFormatter.trimText(chapter.getTitle(), TRIM_LEN),
                    Integer.valueOf(chapter.getMetadata().getWordCount()));
            dataSeries1.getData().add(data);
        });
        chart.getData().add(dataSeries1);

        // tooltip (hover box)
        int chapterCount = 0;
        for (final Object item: dataSeries1.getData()) {
            final Data data = (Data) item;
            final Chapter chapter = book.getChapters().get(chapterCount++);
            final String wordcount = StringFormatter.formatInteger(chapter.getMetadata().getWordCount());
            final Tooltip tooltip = new Tooltip(chapter.getTitle() + "\n" + wordcount + " words");
            tooltip.setShowDelay(Duration.seconds(0));
            Tooltip.install(data.getNode(), tooltip);
        }

        // formatting
        chart.setLegend(null);
        chart.setMinHeight(300);
        chart.setMinWidth(35 * book.getChapters().size());

        return chart;
    }

    private ChapterLengthChart(Axis axis, Axis axis1) {
        super(axis, axis1);
    }
}
