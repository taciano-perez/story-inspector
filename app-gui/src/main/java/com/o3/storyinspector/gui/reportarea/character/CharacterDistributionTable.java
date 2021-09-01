package com.o3.storyinspector.gui.reportarea.character;


import com.o3.storyinspector.gui.core.domain.Character;
import com.o3.storyinspector.gui.core.domain.Characters;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.stream.IntStream;

public class CharacterDistributionTable extends TableView {

    static final Logger LOGGER = LoggerFactory.getLogger(CharacterDistributionTable.class);

    Characters characters;

    public CharacterDistributionTable(final Characters characters) {
        super();
        this.characters = characters;
        this.setPlaceholder(new Label("No rows to display"));

        final TableColumn<Character,String> characterNameCol = new TableColumn<>("Character / Chapter");
        characterNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
//        characterNameCol.setCellValueFactory(cell -> {
//            return new ReadOnlyObjectWrapper(cell.getValue().getName());
//        });
        this.getColumns().add(characterNameCol);

        IntStream.range(1, characters.getTotalNumOfChapters()).forEach(
                chapterNum -> {
                    final TableColumn<Character,String> chapterNumberCol = new TableColumn<>(Integer.toString(chapterNum));
                    chapterNumberCol.setCellValueFactory(cell -> {
                        final Character character = cell.getValue();
                        final boolean characterIsInChapter = character.getChapters().contains(chapterNum);
                        return new ReadOnlyObjectWrapper((characterIsInChapter) ? "X" : "");
                    });
                    this.getColumns().add(chapterNumberCol);
                });

        characters.getCharacters().stream()
                .sorted(Comparator.comparingDouble(Character::getTotalPercentageOfChapters).reversed())
                .forEach(character -> {
                    LOGGER.info("adding character " + character.getName());
                    this.getItems().add(character);
                });

        // formatting
        this.setMinHeight(300);
        this.setMinWidth(35 * characters.getTotalNumOfChapters());
    }
}
