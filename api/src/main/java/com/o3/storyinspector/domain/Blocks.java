package com.o3.storyinspector.domain;

import com.o3.storyinspector.annotation.blocks.SentenceSplitter;
import com.o3.storyinspector.annotation.readability.FleschKincaidReadabilityInspector;
import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.Book;

import java.util.ArrayList;
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

    public static Blocks buildBlocks(final Book book, final String bookTitle, final String author) {
        final List<Block> blockList = new ArrayList<>();
        int chapterId = 1;
        int blockId = 1;
        for (final com.o3.storyinspector.storydom.Chapter chapter : book.getChapters()) {
            final String chapterTitle = "Chapter #" + chapterId++ + " " + chapter.getTitle();
            for (final com.o3.storyinspector.storydom.Block domBlock : chapter.getBlocks()) {
                final List<Sentence> sentences = new ArrayList<>();
                for (final String sentenceText : SentenceSplitter.splitSentences(domBlock)) {
                    final int wordCount = WordCountInspector.inspectWordCount(sentenceText);
                    double fkGradeLevel = FleschKincaidReadabilityInspector.inspectFKGradeLevel(sentenceText);
                    sentences.add(new Sentence(sentenceText, fkGradeLevel, wordCount));
                }
                blockList.add(new Block(blockId++, domBlock, chapterTitle, sentences));
            }
        }
        return new Blocks(bookTitle, author, blockList);
    }

}
