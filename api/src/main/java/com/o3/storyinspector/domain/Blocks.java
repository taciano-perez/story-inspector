package com.o3.storyinspector.domain;

import java.util.List;

public class Blocks {

    private String bookTitle;
    private String bookAuthor;
    private List<Block> blocks;

    public Blocks(String bookTitle, String bookAuthor, List<Block> blocks) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.blocks = blocks;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}
