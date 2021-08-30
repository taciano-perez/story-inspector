package com.o3.storyinspector.gui.processbook;

import com.o3.storyinspector.gui.core.BookCore;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AnnotateBookTask extends Task<StorydomData> {

    StorydomData storydomData;

    public static void annotateStoryDom(final StorydomData data) {
        final Thread th = new Thread(new AnnotateBookTask(data));
        th.setDaemon(true);
        th.start();
    }

    public AnnotateBookTask(final StorydomData storydomData) {
        this.storydomData = storydomData;
    }

    @Override
    protected StorydomData call() throws Exception {
        // create StoryDOM and write file
        final File inputFile = new File(storydomData.getInputFilename());
        final String xml = BookCore.importStorydomFromFile(inputFile);
        final String author = storydomData.getAuthor().replaceAll(" ", "_");
        final String title = storydomData.getTitle().replaceAll(" ", "_");
        final String storydomFilename = author + "-" + title + ".storydom";
        final String currentPath = new java.io.File(".").getCanonicalPath();
        final File storydomFile = new File(currentPath, storydomFilename);
        writeXmlToFile(xml, storydomFile);

        // annotate StoryDOM
        final String annotatedStorydom = BookCore.annotateStorydom(xml);
        writeXmlToFile(annotatedStorydom, storydomFile);
        return storydomData;
    }

    private static void writeXmlToFile(final String xml, final File file) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.append(xml);
        writer.close();
    }
}
