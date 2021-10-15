package com.o3.storyinspector.gui.core.domain;

public class Sentence {

    private String body;
    private Double fkGrade;
    private Integer wordCount;

    public Sentence(String body, Double fkGrade, Integer wordCount) {
        this.body = body;
        this.fkGrade = fkGrade;
        this.wordCount = wordCount;
    }

    public String getBody() {
        return body;
    }

    public Double getFkGrade() {
        return fkGrade;
    }

    public Integer getWordCount() {
        return wordCount;
    }
}
