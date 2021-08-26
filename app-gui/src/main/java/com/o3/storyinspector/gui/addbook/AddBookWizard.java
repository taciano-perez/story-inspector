package com.o3.storyinspector.gui.addbook;

import com.o3.storyinspector.gui.utils.I18N;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

public class AddBookWizard {

    static String ADD_BOOK_WIZ_TITLE = "addBookWizTitle";

    Wizard wizard;
    WizardPane page1;
    StepBookProperties page2;
    WizardPane page3;

    public static void addNewBook(final Stage window) {
        AddBookWizard addBookWizard = new AddBookWizard(window);
        addBookWizard.showAndWait();
    }

    public AddBookWizard(final Stage window) {
        wizard = new Wizard();
        wizard.setTitle(I18N.stringFor(ADD_BOOK_WIZ_TITLE));

        // page 1
        page1 = new StepDisclaimer(wizard);
        page2 = new StepBookProperties(wizard, window);
        page3 = new StepBookStructure(wizard);

        wizard.setFlow(new Wizard.LinearFlow(page1, page2, page3));
    }


    public void showAndWait() {
        // show wizard and wait for response
        wizard.showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                System.out.println("Wizard finished, settings: " + wizard.getSettings());
            }
        });
    }


}
