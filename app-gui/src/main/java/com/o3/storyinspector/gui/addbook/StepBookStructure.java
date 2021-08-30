package com.o3.storyinspector.gui.addbook;

import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.gui.core.BookCore;
import com.o3.storyinspector.gui.utils.I18N;
import com.o3.storyinspector.storydom.Chapter;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.File;
import java.util.List;

public class StepBookStructure extends WizardPane {

    static String BOOK_STRUCT_CHAPTER_AREA = "bookStructChapterArea";
    static String BOOK_STRUCT_INTRO = "bookStructIntro";
    static String BOOK_STRUCT_CHECK = "bookStructCheck";
    static String BOOK_STRUCT_END = "bookStructEnd";

    TextArea bookChapters;
    Label headerLabel;
    CheckBox bookStructCheck;

    int numOfChapters = 0;

    public StepBookStructure(final Wizard wizard) {
        headerLabel = new Label();
        updateHeaderLabel(wizard);
        bookChapters = new TextArea(I18N.stringFor(BOOK_STRUCT_CHAPTER_AREA));
        final Label emptyLineLabel1 = new Label("\n");
        bookStructCheck = new CheckBox(I18N.stringFor(BOOK_STRUCT_CHECK));
        bookStructCheck.setOnAction(e -> wizard.setInvalid(!bookStructCheck.isSelected()));
        final Label emptyLineLabel2 = new Label("\n");
        final Label trailerLabel = new Label(I18N.stringFor(BOOK_STRUCT_END));
        trailerLabel.setFont(Font.font(Font.getDefault().getFamily(), FontPosture.ITALIC, Font.getDefault().getSize()));
        final VBox layout = new VBox();
        layout.getChildren().addAll(headerLabel, bookChapters, emptyLineLabel1, bookStructCheck, emptyLineLabel2, trailerLabel);
        this.setContent(layout);
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        final File bookFile = (File) wizard.getProperties().get(AddBookWizard.PROP_SELECTED_FILE);
        bookChapters.setText(getChapterList(bookFile));
        bookChapters.setEditable(false);

        bookStructCheck.setSelected(false);
        wizard.setInvalid(true);

        updateHeaderLabel(wizard);
    }

    private void updateHeaderLabel(final Wizard wizard) {
        final String bookTitle = (String) wizard.getProperties().get(AddBookWizard.PROP_BOOK_TITLE);
        final String bookAuthor = (String) wizard.getProperties().get(AddBookWizard.PROP_BOOK_AUTHOR);
        headerLabel.setText(String.format(I18N.stringFor(BOOK_STRUCT_INTRO), bookTitle, bookAuthor, numOfChapters));
    }

    private String getChapterList(final File bookFile) {
        String chapterListAsString = "";
        int chapterCount = 0;
        final List<Chapter> chapters = BookCore.readChapterList(bookFile);
        for (final Chapter chapter : chapters) {
            final long chapterWordcount = WordCountInspector.inspectChapterWordCount(chapter);
            chapterListAsString += "" + ++chapterCount + "- " + chapter.getTitle() + " (" + chapterWordcount + " words)\n";
        }
        numOfChapters = chapterCount;
        return chapterListAsString;
    }
}
