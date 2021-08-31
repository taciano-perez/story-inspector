package com.o3.storyinspector.gui.reportarea;

import com.o3.storyinspector.gui.core.*;
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
