package com.o3.storyinspector.domain;

public class Block {

    public static final double FK_GRADE_UNKNOWN = -256;

    private Integer id;
    private String body;
    private String chapterName;
    private Double fkGrade;

    public Block(final Integer id, final com.o3.storyinspector.storydom.Block domBlock, final String chapterName) {
        this.body = domBlock.getBody();
        this.fkGrade = (domBlock.getFkGrade() != null) ? domBlock.getFkGrade().doubleValue() : FK_GRADE_UNKNOWN;
        this.id = id;
        this.chapterName = chapterName;
    }

    public Block(final com.o3.storyinspector.storydom.Block domBlock, final String chapterName) {
        this.body = domBlock.getBody();
        this.fkGrade = (domBlock.getFkGrade() != null) ? domBlock.getFkGrade().doubleValue() : FK_GRADE_UNKNOWN;
        this.id = (this.id != null) ? Integer.valueOf(domBlock.getId()) : 0;
        this.chapterName = chapterName;
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
}
