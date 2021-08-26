package com.o3.storyinspector.gui.addbook;

import com.o3.storyinspector.gui.core.BookController;
import javafx.scene.control.TextArea;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.File;

public class StepBookStructure extends WizardPane {

    TextArea bookChapters;

    public StepBookStructure(final Wizard wizard) {
        bookChapters = new TextArea("Processing...");
        this.setContent(bookChapters);
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        final String xml = BookController.importStorydomFromFile((File) wizard.getProperties().get("SEL_FILE"));
        bookChapters.setText(xml);
    }
}
