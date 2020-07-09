package com.o3.storyinspector.annotation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

class AnnotationEngineTest {

    private static final String INPUT_STORYDOM_PATH = AnnotationEngine.class.getResource("/storydom-a-study-in-scarlett.xml").getPath().replaceFirst("/", "");
    private static final String ANNOTATED_STORYDOM_PATH = "./target/annotated-storydom-a-study-in-scarlett.xml";

    private static final String INPUT_PUNK_ROMANA = AnnotationEngine.class.getResource("/storydom-punk-romana-dave-kavanaugh.xml").getPath().replaceFirst("/", "");
    private static final String ANNOTATED_PUNK_ROMANA = "./target/annotated-storydom-punk-romana-dave-kavanaugh.xml";

    private static final String INPUT_WINTER = AnnotationEngine.class.getResource("/storydom-winter-juho-finn.xml").getPath().replaceFirst("/", "");
    private static final String ANNOTATED_WINTER = "./target/annotated-winter-juho-finn.xml";

    @Disabled
    @Test
    public void testAnnotateBook() throws IOException, JAXBException {
        AnnotationEngine.annotateBook(INPUT_STORYDOM_PATH, ANNOTATED_STORYDOM_PATH);
    }

    @Disabled
    @Test
    public void testAnnotatePunkRomana() throws IOException, JAXBException {
        AnnotationEngine.annotateBook(INPUT_PUNK_ROMANA, ANNOTATED_PUNK_ROMANA);
    }

    @Disabled
    @Test
    public void testAnnotateWinter() throws IOException, JAXBException {
        AnnotationEngine.annotateBook(INPUT_WINTER, ANNOTATED_WINTER);
    }

}