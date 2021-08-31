package com.o3.storyinspector.gui.processbook;

import com.o3.storyinspector.gui.core.BookEvent;
import com.o3.storyinspector.gui.core.BookManager;
import com.o3.storyinspector.gui.status.TaskManager;
import com.o3.storyinspector.gui.utils.I18N;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AnnotateBookGuiTask extends Task<StorydomData> {

    static String BOOK_BY = "bookBy";
    static String BOOK_PROCESSING = "bookProcessing";
    static String BOOK_TASK_MSG = "bookTaskMessage";

    StorydomData storydomData;

    public static void annotateStoryDom(final StorydomData data) {
        final AnnotateBookGuiTask task = new AnnotateBookGuiTask(data);
        final Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        TaskManager.getInstance().addTask(task);
        BookManager.fireBookEvent(new BookEvent(BookEvent.BOOK_QUEUED));
    }

    public AnnotateBookGuiTask(final StorydomData storydomData) {
        this.storydomData = storydomData;
    }

    @Override
    protected StorydomData call() throws Exception {
        // create StoryDOM and write file
        final File inputFile = new File(storydomData.getInputFilename());
        final String xml = BookManager.importStorydomFromFile(inputFile, storydomData.getTitle(), storydomData.getAuthor());
        final String author = storydomData.getAuthor().replaceAll(" ", "_");
        final String title = storydomData.getTitle().replaceAll(" ", "_");
        final String storydomFilename = author + "-" + title + "." + BookManager.STORYDOM_EXTENSION;
        final File storydomFile = new File(BookManager.getBookLibraryDir(), storydomFilename);
        writeXmlToFile(xml, storydomFile);

        // annotate StoryDOM
        updateTitle(storydomData.getTitle() + " " + I18N.stringFor(BOOK_BY) + "  " + storydomData.getAuthor());
        updateMessage(I18N.stringFor(BOOK_PROCESSING));
        final String annotatedStorydom = BookManager.annotateStorydom(xml, (percentageCompleted, minutesLeft) -> {
            final String msg = String.format(I18N.stringFor(BOOK_TASK_MSG), ((int) Math.floor(percentageCompleted * 100)), minutesLeft);
            System.out.println(msg);    // FIXME
            updateProgress((percentageCompleted * minutesLeft), minutesLeft);
            updateMessage(msg);
        });
        writeXmlToFile(annotatedStorydom, storydomFile);
        return storydomData;
    }

    private static void writeXmlToFile(final String xml, final File file) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.append(xml);
        writer.close();
    }
}
