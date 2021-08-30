package com.o3.storyinspector.gui.addbook;

import com.o3.storyinspector.gui.processbook.AnnotateBookTask;
import com.o3.storyinspector.gui.processbook.StorydomData;
import com.o3.storyinspector.gui.utils.I18N;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.controlsfx.dialog.Wizard;

import java.io.File;

public class AddBookWizard {

    public static String PROP_BOOK_TITLE = "PROP_BOOK_TITLE";
    public static String PROP_BOOK_AUTHOR = "PROP_BOOK_AUTHOR";
    public static String PROP_SELECTED_FILE = "PROP_SELECTED_FILE";

    static String ADD_BOOK_WIZ_TITLE = "addBookWizTitle";

    Wizard wizard;
    StepDisclaimer page1;
    StepBookProperties page2;
    StepBookStructure page3;
    StepSummary page4;


    public static void addNewBook(final Stage window) {
        AddBookWizard addBookWizard = new AddBookWizard(window);
        addBookWizard.showAndWait();
    }

    public AddBookWizard(final Stage window) {
        wizard = new Wizard();
        wizard.setTitle(I18N.stringFor(ADD_BOOK_WIZ_TITLE));

        page1 = new StepDisclaimer(wizard, window);
        page2 = new StepBookProperties(wizard, window);
        page3 = new StepBookStructure(wizard);
        page4 = new StepSummary(wizard);

        wizard.setFlow(new Wizard.LinearFlow(page1, page2, page3, page4));
    }


    public void showAndWait() {
        // show wizard and wait for response
        wizard.showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                System.out.println("Wizard finished, settings: " + wizard.getSettings());

                // trigger annotate book task
                final String filePath = ((File) wizard.getProperties().get(PROP_SELECTED_FILE)).getAbsolutePath();
                final StorydomData storydomData = new StorydomData((String)wizard.getProperties().get(PROP_BOOK_TITLE),
                        (String)wizard.getProperties().get(PROP_BOOK_AUTHOR),
                        filePath);
                AnnotateBookTask.annotateStoryDom(storydomData);
            }
        });
    }


}
