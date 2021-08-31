package com.o3.storyinspector.gui.reportarea;

import com.o3.storyinspector.gui.status.TaskManager;
import com.o3.storyinspector.gui.utils.I18N;
import javafx.scene.control.Tab;
import org.controlsfx.control.TaskProgressView;

public class TaskTab extends Tab {

    static String REPORT_TASK_TAB_TITLE = "reportTaskTabTitle";

    TaskProgressView taskProgressView;

    public TaskTab() {
        super(I18N.stringFor(REPORT_TASK_TAB_TITLE));
        taskProgressView = TaskManager.getInstance().getTaskProgressView();
        this.setContent(taskProgressView);
    }
}
