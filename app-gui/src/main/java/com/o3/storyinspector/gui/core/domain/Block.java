package com.o3.storyinspector.gui.core.domain;

import com.o3.storyinspector.storydom.Chapter;

import java.util.List;

public class Block {

    public static final double FK_GRADE_UNKNOWN = -256;

    private Integer id;
    private String body;
    private Double fkGrade;
    private List<Sentence> sentences;
    private Chapter chapter;

    public Block(final Integer id, final com.o3.storyinspector.storydom.Block domBlock, final Chapter chapter, final List<Sentence> sentences) {
        this.body = domBlock.getBody();
        this.fkGrade = (domBlock.getFkGrade() != null) ? domBlock.getFkGrade().doubleValue() : FK_GRADE_UNKNOWN;
        this.id = id;
        this.chapter = chapter;
        this.sentences = sentences;
    }

    public Integer getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public Double getFkGrade() {
        return fkGrade;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }
}
