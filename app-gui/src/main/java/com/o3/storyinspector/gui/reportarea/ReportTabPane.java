package com.o3.storyinspector.gui.reportarea;

import com.o3.storyinspector.gui.core.*;
import com.o3.storyinspector.gui.reportarea.character.CharacterReportTab;
import com.o3.storyinspector.gui.reportarea.emotion.EmotionReportTab;
import com.o3.storyinspector.gui.reportarea.structure.BookStructureReportTab;
import com.o3.storyinspector.gui.reportarea.task.TaskTab;
import javafx.scene.control.TabPane;

public class ReportTabPane extends TabPane implements ReportEventListener, BookEventListener {

    TaskTab taskTab;

    public ReportTabPane() {
        ReportManager.registerEventListener(this);
        BookManager.registerEventListener(this);
    }

    @Override
    public void handleReportEvent(ReportEvent event) {
        if (event.getType() == ReportEvent.OPEN_REPORT_BOOK_STRUCTURE) {
            final BookStructureReportTab bookStructureReportTab = new BookStructureReportTab(event.getBook());
            this.getTabs().add(bookStructureReportTab);
            this.getSelectionModel().selectLast();  // focus on new tab
        } else if (event.getType() == ReportEvent.OPEN_REPORT_CHARACTER) {
            final CharacterReportTab characterReportTab = new CharacterReportTab(event.getBook());
            this.getTabs().add(characterReportTab);
            this.getSelectionModel().selectLast();  // focus on new tab
        } else if (event.getType() == ReportEvent.OPEN_REPORT_EMOTION) {
            final EmotionReportTab emotionReportTab = new EmotionReportTab(event.getBook());
            this.getTabs().add(emotionReportTab);
            this.getSelectionModel().selectLast();  // focus on new tab
        }
    }

    @Override
    public void handleEvent(BookEvent event) {
        if (event.getType() == BookEvent.BOOK_QUEUED) {
            if (taskTab == null) {
                this.getTabs().add(new TaskTab());
            }
        }
    }
}
