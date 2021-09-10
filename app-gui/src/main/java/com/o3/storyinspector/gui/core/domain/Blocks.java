package com.o3.storyinspector.gui.core.domain;

import com.o3.storyinspector.annotation.blocks.SentenceSplitter;
import com.o3.storyinspector.annotation.readability.FleschKincaidReadabilityInspector;
import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.Book;

import java.util.ArrayList;
import java.util.List;

public class Blocks {

    private static double FK_GRADE_UNKNOWN = -256;

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
//            final String chapterTitle = "Chapter #" + chapterId++ + " " + chapter.getTitle();
            for (final com.o3.storyinspector.storydom.Block domBlock : chapter.getBlocks()) {
                final List<Sentence> sentences = new ArrayList<>();
                for (final String sentenceText : SentenceSplitter.splitSentences(domBlock)) {
                    final int wordCount = WordCountInspector.inspectWordCount(sentenceText);
                    double fkGradeLevel = FleschKincaidReadabilityInspector.inspectFKGradeLevel(sentenceText);
                    sentences.add(new Sentence(sentenceText, fkGradeLevel, wordCount));
                }
                blockList.add(new Block(blockId++, domBlock, chapter, sentences));
            }
        }
        return new Blocks(bookTitle, author, blockList);
    }

    public static String getFkGradeMessage(final double fkGrade) {
        String fkGradeMsg;
        if (fkGrade == FK_GRADE_UNKNOWN) {
            fkGradeMsg = "(Readability not calculated. Re-upload your book to calculate.)";
        } else if (fkGrade <= 0.99) {
            fkGradeMsg = "Extremely easy to read.";
        } else if (fkGrade <= 1.99) {
            fkGradeMsg = "Grade 1. Very easy to read.";
        } else if (fkGrade <= 2.99) {
            fkGradeMsg = "Grade 2. Very easy to read.";
        } else if (fkGrade <= 3.99) {
            fkGradeMsg = "Grade 3. Very easy to read.";
        } else if (fkGrade <= 4.99) {
            fkGradeMsg = "Grade 4. Very easy to read.";
        } else if (fkGrade <= 5.99) {
            fkGradeMsg = "Grade 5. Very easy to read.";
        } else if (fkGrade <= 6.99) {
            fkGradeMsg = "Grade 6. Easy to read. Conversational English for consumers.";
        } else if (fkGrade <= 7.99) {
            fkGradeMsg = "Grade 7. Fairly easy to read.";
        } else if (fkGrade <= 8.99) {
            fkGradeMsg = "Grade 8. Plain English.";
        } else if (fkGrade <= 9.99) {
            fkGradeMsg = "Grade 9. Plain English.";
        } else if (fkGrade <= 10.99) {
            fkGradeMsg = "Grade 10. Fairly difficult to read.";
        } else if (fkGrade <= 11.99) {
            fkGradeMsg = "Grade 11. Fairly difficult to read.";
        } else if (fkGrade <= 12.99) {
            fkGradeMsg = "Grade 12. Fairly difficult to read.";
        } else if (fkGrade <= 21.99) {
            fkGradeMsg = "College grade. Difficult to read.";
        } else if (fkGrade >= 22) {
            fkGradeMsg = "Professional, extremely difficult to read.";
        } else {
            fkGradeMsg = "(Readability not calculated. Re-upload your book to calculate.)";
        }
        return fkGradeMsg;
    }

}
