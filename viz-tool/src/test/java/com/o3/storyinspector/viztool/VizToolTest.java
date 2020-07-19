package com.o3.storyinspector.viztool;

import org.junit.jupiter.api.Test;

class VizToolTest {

    private static final String INPUT_ANNOTATED_STORYDOM_PATH = VizToolTest.class.getResource("/annotated-storydom-a-study-in-scarlett.xml").getPath().replaceFirst("/", "");
    private static final String OUTPUT_PATH = VizToolTest.class.getResource("/").getPath().replaceFirst("/", "");

    @Test
    void booktoHtml() throws Exception {
        // when
        VizTool.storyDomToHtml(INPUT_ANNOTATED_STORYDOM_PATH, OUTPUT_PATH);
    }

}