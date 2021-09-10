package com.o3.storyinspector.gui.reportarea.sentence;

import com.o3.storyinspector.gui.utils.IconUtils;
import com.o3.storyinspector.storydom.Book;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class SentenceVarietyReportTab extends Tab {

    public SentenceVarietyReportTab(final Book book) {
        super(book.getTitle() + " (Sentence Variety)");
        this.setGraphic(IconUtils.getIcon(FontAwesome.Glyph.EDIT));

        VBox stackedTitledPanes = new VBox();

        // chapter length chart
        final TitledPane tab1 = new TitledPane("Tab", null);
        tab1.setExpanded(true);
        stackedTitledPanes.getChildren().add(tab1);

        // scrollable content
        final ScrollPane scrollPane = new ScrollPane(stackedTitledPanes);
        scrollPane.fitToWidthProperty().set(true);
        scrollPane.fitToHeightProperty().set(true);

        this.setContent(scrollPane);
    }

}
