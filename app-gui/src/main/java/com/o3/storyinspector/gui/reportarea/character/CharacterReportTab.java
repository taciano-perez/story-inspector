package com.o3.storyinspector.gui.reportarea.character;

import com.o3.storyinspector.gui.core.domain.Character;
import com.o3.storyinspector.gui.core.domain.Characters;
import com.o3.storyinspector.gui.utils.IconUtils;
import com.o3.storyinspector.storydom.Book;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;

public class CharacterReportTab extends Tab {

    Book book;
    Characters characters;

    public  CharacterReportTab(final Book book) {
        super(book.getTitle() + " (Characters)");
        this.setGraphic(IconUtils.getIcon(FontAwesome.Glyph.USERS));
        this.book = book;
        this.characters = Characters.buildFromBook(book);

        final VBox stackedTitledPanes = new VBox();

        // summary
        final VBox summaryLayout = new VBox();

        final int characterCount = this.characters.getCharacters().size();
        summaryLayout.getChildren().add(new Label(String.format("This book has %d characters in total.", characterCount)));
        final TitledPane summaryPane = new TitledPane("Summary", summaryLayout);

        summaryLayout.getChildren().add(new Label("\n"));
        summaryLayout.getChildren().add(new Label("The main characters (>=50% of chapters) are:"));
        characters.getCharacters().stream()
                .filter(character -> character.getTotalPercentageOfChapters() >= 0.5)
                .map(Character::getName)
                .sorted()
                .forEach(name ->  summaryLayout.getChildren().add(new Label(" - " + name)));

        summaryLayout.getChildren().add(new Label("\n"));
        summaryLayout.getChildren().add(new Label("The secondary characters (>=20% of chapters) are:"));
        characters.getCharacters().stream()
                .filter(character -> character.getTotalPercentageOfChapters() >= 0.2
                        && character.getTotalPercentageOfChapters() < 0.5)
                .map(Character::getName)
                .sorted()
                .forEach(name -> summaryLayout.getChildren().add(new Label(" - " + name)));

        summaryPane.setExpanded(true);
        stackedTitledPanes.getChildren().add(summaryPane);

        // chart: character distribution (total)
        final CharacterDistributionChart characterDistributionChart = CharacterDistributionChart.build(characters);
        final ScrollPane chartScrollPane = new ScrollPane(characterDistributionChart);
        chartScrollPane.setMinHeight(characterDistributionChart.getMinHeight() * 1.4);
        final TitledPane characterDistributionChartPane =
                new TitledPane("Characters' Distribution (Total)",
                        chartScrollPane);
        characterDistributionChartPane.setExpanded(true);
        stackedTitledPanes.getChildren().add(characterDistributionChartPane);

        // table: character distribution per chapter
        final CharacterDistributionTable characterDistributionTable =
                new CharacterDistributionTable(characters);
        stackedTitledPanes.getChildren().add(
                new TitledPane("Characters' Distribution (Per Chapter)",
                        characterDistributionTable));

        // scrollable content
        final ScrollPane scrollPane = new ScrollPane(stackedTitledPanes);
        scrollPane.fitToWidthProperty().set(true);
        scrollPane.fitToHeightProperty().set(true);

        this.setContent(scrollPane);
    }
}
