package com.o3.storyinspector.db;

public class BookDAO {
    private long id;
    private String title;
    private String author;
    private String rawInput;

    public BookDAO(long id, String title, String author, String rawInput) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.rawInput = rawInput;
    }

    public BookDAO(String title, String author, String rawInput) {
        this.title = title;
        this.author = author;
        this.rawInput = rawInput;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRawInput() {
        return rawInput;
    }

    public void setRawInput(String rawInput) {
        this.rawInput = rawInput;
    }
}