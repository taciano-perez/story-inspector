package com.o3.storyinspector.gui.addbook;

import com.o3.storyinspector.gui.utils.I18N;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

public class StepSummary extends WizardPane {

    static String BOOK_QUEUED_INTRO = "bookQueuedIntro";

    Label summaryLabel;

    public StepSummary(final Wizard wizard) {
        summaryLabel = new Label(I18N.stringFor(BOOK_QUEUED_INTRO));

        final VBox layout = new VBox();
        layout.getChildren().addAll(summaryLabel);
        this.setContent(layout);
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        final String bookTitle = (String) wizard.getProperties().get(AddBookWizard.PROP_BOOK_TITLE);
        final String bookAuthor = (String) wizard.getProperties().get(AddBookWizard.PROP_BOOK_AUTHOR);
        summaryLabel.setText(String.format(I18N.stringFor(BOOK_QUEUED_INTRO), bookTitle, bookAuthor));
    }
}
