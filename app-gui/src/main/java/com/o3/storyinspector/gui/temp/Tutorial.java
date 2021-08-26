package com.o3.storyinspector.gui.temp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class Tutorial extends Application {

    Button button1, button2, button3;
    Scene scene1, scene2;
    Label label1, label2;

    @Override
    public void start(final Stage window) {

        label1 = new Label("First scene");
        button1 = new Button("go to scene 2");
        button1.setOnAction(e -> window.setScene(scene2));
        button3 = new Button("Open dialog...");
        button3.setOnAction(e -> AlertBox.display("Alert", "This is an alert"));
        final VBox layout1 = new VBox(20);
        layout1.getChildren().addAll(label1, button1, button3);
        scene1 = new Scene(layout1, 640, 480);

        label2 = new Label("Second scene");
        button2 = new Button("back to scene 1");
        button2.setOnAction(e -> window.setScene(scene1));
        final VBox layout2 = new VBox(20);
        layout2.getChildren().addAll(label2, button2);
        scene2 = new Scene(layout2, 640, 480);

        window.setTitle("Story Inspector");
        window.setScene(scene1);
        window.show();
    }

    public static void main(final String[] args) {
        launch();
    }

}