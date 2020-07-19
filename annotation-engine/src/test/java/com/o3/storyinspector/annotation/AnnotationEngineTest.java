package com.o3.storyinspector.annotation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

class AnnotationEngineTest {

    private static final String INPUT_STORYDOM_PATH = AnnotationEngine.class.getResource("/storydom-a-study-in-scarlett.xml").getPath().replaceFirst("/", "");
    private static final String ANNOTATED_STORYDOM_PATH = "./target/annotated-storydom-a-study-in-scarlett.xml";

    @Disabled
    @Test
    public void testAnnotateBook() throws IOException, JAXBException {
        AnnotationEngine.annotateBook(INPUT_STORYDOM_PATH, ANNOTATED_STORYDOM_PATH);
    }

}