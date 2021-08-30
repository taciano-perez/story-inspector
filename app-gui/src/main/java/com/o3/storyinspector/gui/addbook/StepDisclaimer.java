package com.o3.storyinspector.gui.addbook;

import com.o3.storyinspector.gui.utils.I18N;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

public class StepDisclaimer extends WizardPane {

    static String ADD_BOOK_WIZ_DISCLAIMER = "addBookWizDisclaimer";
    static String ADD_BOOK_WIZ_DISCLAIMER_CHECK = ADD_BOOK_WIZ_DISCLAIMER + "Check";

    CheckBox disclaimerCheckbox;

    public StepDisclaimer(final Wizard wizard, final Stage window) {
        final Label disclaimerLabel = new Label(I18N.stringFor(ADD_BOOK_WIZ_DISCLAIMER));
        disclaimerCheckbox = new CheckBox(I18N.stringFor(ADD_BOOK_WIZ_DISCLAIMER_CHECK));
        disclaimerCheckbox.setSelected(false);
        disclaimerCheckbox.setOnAction(e -> wizard.setInvalid(!disclaimerCheckbox.isSelected()));

        final VBox layout = new VBox();
        layout.getChildren().addAll(disclaimerLabel, disclaimerCheckbox);
        this.setContent(layout);
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        wizard.setInvalid(!disclaimerCheckbox.isSelected());
    }
}
