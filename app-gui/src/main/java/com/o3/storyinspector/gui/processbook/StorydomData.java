package com.o3.storyinspector.gui.processbook;

/**
 * Data of a StoryDOM file (filename, title, author)
 */
public class StorydomData {

    private String title;
    private String author;
    private String inputFilename;

    public StorydomData(String title, String author, String inputFilename) {
        this.title = title;
        this.author = author;
        this.inputFilename = inputFilename;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getInputFilename() {
        return inputFilename;
    }
}
