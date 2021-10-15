package com.o3.storyinspector.gui;

import com.o3.storyinspector.gui.addbook.AddBookWizard;
import com.o3.storyinspector.gui.booktree.BookTree;
import com.o3.storyinspector.gui.reportarea.ReportTabPane;
import com.o3.storyinspector.gui.skin.Styles;
import com.o3.storyinspector.gui.status.StoryInspectorStatusBar;
import com.o3.storyinspector.gui.utils.I18N;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;

/**
 * Main StoryInspector GUI application
 */
public class StoryInspectorGui extends Application {

    static final Logger LOGGER = LoggerFactory.getLogger(StoryInspectorGui.class);

    static String STORY_INSPECTOR_TITLE = "storyInspectorTitle";
    static String BOOK_MENU = "bookMenu";
    static String BOOK_MENU_QUIT = "bookMenuQuit";
    static String BOOK_MENU_ADD = "bookMenuAdd";

    static String ICON_FILENAME = "/logo.png";

    Stage window;
    MenuBar menu;
    TreeView bookTree;
    ReportTabPane reportArea;
    StatusBar statusBar;

    @Override
    public void start(final Stage window) throws Exception {
        LOGGER.info("Initializing application");

        // init
        this.window = window;
        this.menu = new MenuBar();
        LOGGER.info("Initializing book tree...");
        this.bookTree = new BookTree();
        this.reportArea = new ReportTabPane();
        this.statusBar = new StoryInspectorStatusBar();

        // menus
        final Menu bookMenu = new Menu(I18N.stringFor(BOOK_MENU));
        final MenuItem bookMenuAdd = new MenuItem(I18N.stringFor(BOOK_MENU_ADD));
        bookMenuAdd.setOnAction(e -> AddBookWizard.addNewBook(window));
        final MenuItem bookMenuQuit = new MenuItem(I18N.stringFor(BOOK_MENU_QUIT));
        bookMenuQuit.setOnAction(e -> quit());
        bookMenu.getItems().addAll(bookMenuAdd, bookMenuQuit);
        menu.getMenus().add(bookMenu);

        // layout
        final BorderPane mainWindowLayout = new BorderPane();
        mainWindowLayout.setTop(menu);
        mainWindowLayout.setLeft(bookTree);
        mainWindowLayout.setCenter(reportArea);
        mainWindowLayout.setBottom(statusBar);

        // window
        window.setOnCloseRequest(e -> quit());
        window.getIcons().add(new Image(getClass().getResourceAsStream(ICON_FILENAME)));

        final Scene mainScene = new Scene(mainWindowLayout);
        window.setTitle(I18N.stringFor(STORY_INSPECTOR_TITLE));
        mainScene.getStylesheets().add(getResourceURL(Styles.STORY_INSPECTOR_CSS).toExternalForm());
        window.setMinWidth(900);
        window.setScene(mainScene);
        window.show();
    }

//    private void testResourceFile(String path) throws IOException {
//        File resource = new ClassPathResource(path).getFile();
//        String text = new String(Files.readAllBytes(resource.toPath()));
//        LOGGER.info("resource: " + text);
//    }

    private URL getResourceURL(final String path) throws IOException{
        return (new ClassPathResource(path)).getURL();
    }

    private void quit() {
        LOGGER.info("Quitting application");
        window.close();
        Platform.exit();
    }

    public static void main(final String[] args) {
        launch();
    }

}