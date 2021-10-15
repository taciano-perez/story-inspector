package com.o3.storyinspector.gui.reportarea.structure;

import com.o3.storyinspector.gui.skin.Styles;
import com.o3.storyinspector.gui.utils.IconUtils;
import com.o3.storyinspector.gui.utils.StringFormatter;
import com.o3.storyinspector.gui.view.BorderedTitledPane;
import com.o3.storyinspector.gui.view.LineBreakSeparator;
import com.o3.storyinspector.storydom.Book;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class BookStructureReportTab extends Tab {

    public BookStructureReportTab(Book book) {
        super(book.getTitle() + " (Book Structure)");
        this.setGraphic(IconUtils.getIcon(FontAwesome.Glyph.TREE));

        VBox stackedTitledPanes = new VBox();

        // chapter length chart
        final TitledPane chapterLengthChart = new TitledPane("Chapters Length", ChapterLengthChart.build(book));
        chapterLengthChart.setExpanded(true);
        stackedTitledPanes.getChildren().add(chapterLengthChart);

        // dynamic panes per chapter
        book.getChapters().forEach(chapter -> {
            final HBox wordcountBox = new HBox();
            final Node wordcountIcon = IconUtils.getIcon(FontAwesome.Glyph.CALCULATOR);
            final Label wordcountLabel = new Label(" " + StringFormatter.formatInteger(chapter.getMetadata().getWordCount()) + " words");
            wordcountBox.getChildren().addAll(wordcountIcon, wordcountLabel);
            final HBox readabilityBox = new HBox();
            final Node readabilityIcon = IconUtils.getIcon(FontAwesome.Glyph.PENCIL);
            final Label readabilityLabel = new Label("Readability: " + chapter.getMetadata().getFkGrade() + " FK score");
            readabilityBox.getChildren().addAll(readabilityIcon, readabilityLabel);
            final VBox headerBox = new VBox();
            headerBox.getChildren().addAll(wordcountBox, readabilityBox);

            // characters
            final FlowPane layoutCharacters = new FlowPane();
            layoutCharacters.setHgap(5);
            layoutCharacters.setVgap(5);
            chapter.getMetadata().getCharacters().getCharacters()
                    .forEach(character -> {
                        final MenuItem menuItem1 = new MenuItem("Rename Character");
                        final MenuItem menuItem2 = new MenuItem("Delete Character");
                        final MenuButton menuButton = new MenuButton(character.getName(),
                                IconUtils.getIcon(FontAwesome.Glyph.USER),
                                menuItem1, menuItem2);
                        menuButton.getStyleClass().add(Styles.BUTTON_CHARACTER);
                        layoutCharacters.getChildren().add(menuButton);
            });
            final BorderedTitledPane characterPane = new BorderedTitledPane("Characters", layoutCharacters);

            // locations
            final FlowPane layoutLocations = new FlowPane();
            layoutLocations.setHgap(5);
            layoutLocations.setVgap(5);
            chapter.getMetadata().getLocations().getLocations()
                    .forEach( location -> {
                        final MenuItem menuItem1 = new MenuItem("Rename Location");
                        final MenuItem menuItem2 = new MenuItem("Delete Location");
                        final MenuButton menuButton = new MenuButton(location.getName(),
                                IconUtils.getIcon(FontAwesome.Glyph.MAP_MARKER),
                                menuItem1, menuItem2);
                        menuButton.getStyleClass().add(Styles.BUTTON_LOCATION);
                layoutLocations.getChildren().add(menuButton);
            });
            final BorderedTitledPane locationPane = new BorderedTitledPane("Locations", layoutLocations);

            // complete chapter pane layout
            final VBox vbox = new VBox();
            vbox.getChildren().addAll(headerBox, new LineBreakSeparator(), characterPane, new LineBreakSeparator(), locationPane);
            final TitledPane chapterPane = new TitledPane(chapter.getTitle(), vbox);
            stackedTitledPanes.getChildren().add(chapterPane);
            chapterPane.setExpanded(true);
        });

        // scrollable content
        final ScrollPane scrollPane = new ScrollPane(stackedTitledPanes);
        scrollPane.fitToWidthProperty().set(true);
        scrollPane.fitToHeightProperty().set(true);

        this.setContent(scrollPane);
    }
}
