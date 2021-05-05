package com.o3.storyinspector.domain;

import java.util.List;

public class Block {

    public static final double FK_GRADE_UNKNOWN = -256;

    private Integer id;
    private String body;
    private String chapterName;
    private Double fkGrade;
    private List<Sentence> sentences;

    public Block(final Integer id, final com.o3.storyinspector.storydom.Block domBlock, final String chapterName, final List<Sentence> sentences) {
        this.body = domBlock.getBody();
        this.fkGrade = (domBlock.getFkGrade() != null) ? domBlock.getFkGrade().doubleValue() : FK_GRADE_UNKNOWN;
        this.id = id;
        this.chapterName = chapterName;
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

    public String getChapterName() {
        return chapterName;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }
}
