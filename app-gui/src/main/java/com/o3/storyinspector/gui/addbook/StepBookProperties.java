package com.o3.storyinspector.gui.addbook;

import com.o3.storyinspector.gui.utils.I18N;
import com.o3.storyinspector.gui.utils.StringUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.File;

public class StepBookProperties extends WizardPane {

    static String BOOK_PROPS_TITLE = "bookPropsTitle";
    static String BOOK_PROPS_AUTHOR = "bookPropsAuthor";
    static String BOOK_PROPS_FILE = "bookPropsFile";
    static String BOOK_PROPS_FILE_BTN = "bookPropsFileBtn";

    TextField titleField;
    TextField authorField;
    File selectedFile;
    String selectedFileName = "";

    public StepBookProperties(final Wizard wizard, final Stage window) {
        // grid pane
        final GridPane bookInfoForm = new GridPane();
        bookInfoForm.setAlignment(Pos.CENTER);
        bookInfoForm.setHgap(10);
        bookInfoForm.setVgap(10);
        bookInfoForm.setPadding(new Insets(25, 25, 25, 25));

        // title field
        final Label titleLabel = new Label(I18N.stringFor(BOOK_PROPS_TITLE));
        bookInfoForm.add(titleLabel, 0, 1);
        titleField = TextFields.createClearableTextField();
        titleField.setOnKeyTyped(e -> refreshWizardValidity(wizard));
        bookInfoForm.add(titleField, 1, 1);

        // author field
        final Label authorLabel = new Label(I18N.stringFor(BOOK_PROPS_AUTHOR));
        bookInfoForm.add(authorLabel, 0, 2);
        authorField = TextFields.createClearableTextField();
        authorField.setOnKeyTyped(e -> refreshWizardValidity(wizard));
        bookInfoForm.add(authorField, 1, 2);

        // file field
        final Label fileLabel = new Label(I18N.stringFor(BOOK_PROPS_FILE));
        bookInfoForm.add(fileLabel, 0, 3);
        final FileChooser fileChooser = new FileChooser();
        final TextField selectedFileField = new TextField(selectedFileName);
        selectedFileField.setDisable(true);
        final Button selectFileButton = new Button(I18N.stringFor(BOOK_PROPS_FILE_BTN));
        selectFileButton.setOnAction(e -> {
                selectedFile = fileChooser.showOpenDialog(window);
                if (selectedFile != null) {
                    selectedFileName = selectedFile.getName();
                    selectedFileField.setText(selectedFileName);
                    wizard.getProperties().put("SEL_FILE", selectedFile);
                    refreshWizardValidity(wizard);
                }
            });
        final HBox innerHBox = new HBox();
        innerHBox.getChildren().addAll(selectedFileField, selectFileButton);
        bookInfoForm.add(innerHBox, 1, 3);

        this.setContent(bookInfoForm);
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        refreshWizardValidity(wizard);
    }

    private void refreshWizardValidity(final Wizard wizard) {
        wizard.setInvalid(isPageInvalid());
    }

    private boolean isPageInvalid() {
        return (StringUtils.isEmpty(titleField.getText()) ||
                StringUtils.isEmpty(authorField.getText()) ||
                selectedFile == null);
    }

}
