package com.o3.storyinspector.gui.view;

import com.o3.storyinspector.gui.skin.Styles;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Places content in a bordered pane with a title.
 */
public class BorderedTitledPane extends StackPane {

    public BorderedTitledPane(String titleString, Node content) {
        final Label title = new Label(" " + titleString + " ");
        title.getStyleClass().add(Styles.BORDERED_TITLED_TITLE);
        StackPane.setAlignment(title, Pos.TOP_LEFT);

        final StackPane contentPane = new StackPane();
        content.getStyleClass().add(Styles.BORDERED_TITLED_CONTENT);
        contentPane.getChildren().add(content);

        getStyleClass().add(Styles.BORDERED_TITLED_BORDER);
        getChildren().addAll(title, contentPane);
    }
}
