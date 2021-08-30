package com.o3.storyinspector.gui;

import com.o3.storyinspector.gui.addbook.AddBookWizard;
import com.o3.storyinspector.gui.utils.I18N;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;

/**
 * Main StoryInspector GUI application
 */
public class StoryInspectorGui extends Application {

    static String STORY_INSPECTOR_TITLE = "storyInspectorTitle";
    static String BOOK_MENU = "bookMenu";
    static String BOOK_MENU_QUIT = BOOK_MENU + "Quit";
    static String BOOK_MENU_ADD = BOOK_MENU + "Add";

    static String ICON_FILENAME = "/logo.png";

    MenuBar menu;
    TreeView bookTree;
    ScrollPane reportArea;
    StatusBar statusBar;

    @Override
    public void start(final Stage window) {

        // init
        menu = new MenuBar();
        bookTree = new TreeView();
        reportArea = new ScrollPane();
        statusBar = new StatusBar();

        final Menu bookMenu = new Menu(I18N.stringFor(BOOK_MENU));
        final MenuItem bookMenuAdd = new MenuItem(I18N.stringFor(BOOK_MENU_ADD));
        bookMenuAdd.setOnAction(e -> AddBookWizard.addNewBook(window));
        final MenuItem bookMenuQuit = new MenuItem(I18N.stringFor(BOOK_MENU_QUIT));
        bookMenuQuit.setOnAction(e -> window.close());
        bookMenu.getItems().addAll(bookMenuAdd, bookMenuQuit);
        menu.getMenus().add(bookMenu);

        // layout
        final BorderPane mainWindowLayout = new BorderPane();
        mainWindowLayout.setTop(menu);
        mainWindowLayout.setLeft(bookTree);
        mainWindowLayout.setCenter(reportArea);
        mainWindowLayout.setBottom(statusBar);

        // icon
        window.getIcons().add(new Image(getClass().getResourceAsStream(ICON_FILENAME)));

        final Scene mainScene = new Scene(mainWindowLayout);
        window.setTitle(I18N.stringFor(STORY_INSPECTOR_TITLE));
        window.setMinWidth(800);
        window.setScene(mainScene);
        window.show();
    }

    public static void main(final String[] args) {
        launch();
    }

}