package com.o3.storyinspector.gui.reportarea.emotion;

import com.o3.storyinspector.gui.utils.IconUtils;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.constants.EmotionType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class EmotionReportTab extends Tab {

    public EmotionReportTab(Book book) {
        super(book.getTitle() + " (Emotions)");
        this.setGraphic(IconUtils.getIcon(FontAwesome.Glyph.HEART));

        final VBox stackedTitledPanes = new VBox();

        final TitledPane sentimentChartPane =
                new TitledPane("Sentiment (positive vs. negative):",
                        EmotionChart.buildSentimentChart(book));
        stackedTitledPanes.getChildren().add(sentimentChartPane);


        for (final EmotionType emotionType : EmotionType.values()) {
            final TitledPane emotionChartPane =
                    new TitledPane("Emotion Chart: " + emotionType.asString(),
                    EmotionChart.buildEmotionChart(book, emotionType));
            stackedTitledPanes.getChildren().add(emotionChartPane);
    }

        final ScrollPane scrollPane = new ScrollPane(stackedTitledPanes);
        scrollPane.setFitToWidth(true);
        this.setContent(scrollPane);
    }
}
