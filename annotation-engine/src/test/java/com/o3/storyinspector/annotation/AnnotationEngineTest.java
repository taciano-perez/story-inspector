package com.o3.storyinspector.annotation;

import com.o3.storyinspector.annotation.util.FileUtils;
import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnnotationEngineTest {

    private static final String INPUT_STORYDOM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Book title=\"Example Book\">\n" +
            "    <Chapter id=\"1\" title=\"Chapter 1: A Startling Start.\">\n" +
            "        <Block>\n" +
            "            <Body>This is an example chapter wherein wondrous things would be expected by its eager author.</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "    <Chapter id=\"2\" title=\"Chapter 2: The Unexciting Aftermath.\">\n" +
            "        <Block>\n" +
            "            <Body>This is another example chapter, but the action seems to unfold slower than expected.</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "</Book>\n";

    private static final String EXPECTED_ANNOTATED_STORYDOM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Book title=\"Example Book\">\n" +
            "    <Chapter id=\"1\" title=\"Chapter 1: A Startling Start.\">\n" +
            "        <Metadata wordCount=\"15\" fkGrade=\"9.92666666666667\">\n" +
            "            <Locations/>\n" +
            "            <Characters/>\n" +
            "        </Metadata>\n" +
            "        <Block id=\"1#1\" wordCount=\"15\" sentimentScore=\"-0.06\" fkGrade=\"9.92666666666667\">\n" +
            "            <Emotion type=\"anger\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"anticipation\" score=\"0.08693333333333333\"/>\n" +
            "            <Emotion type=\"disgust\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"fear\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"sadness\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"surprise\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"trust\" score=\"0.0672\"/>\n" +
            "            <Body>This is an example chapter wherein wondrous things would be expected by its eager author.</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "    <Chapter id=\"2\" title=\"Chapter 2: The Unexciting Aftermath.\">\n" +
            "        <Metadata wordCount=\"14\" fkGrade=\"11.784285714285716\">\n" +
            "            <Locations/>\n" +
            "            <Characters/>\n" +
            "        </Metadata>\n" +
            "        <Block id=\"2#1\" wordCount=\"14\" sentimentScore=\"-0.056\" fkGrade=\"11.784285714285716\">\n" +
            "            <Emotion type=\"anger\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"anticipation\" score=\"0.07978571428571428\"/>\n" +
            "            <Emotion type=\"disgust\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"fear\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"sadness\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"surprise\" score=\"0.0\"/>\n" +
            "            <Emotion type=\"trust\" score=\"0.0\"/>\n" +
            "            <Body>This is another example chapter, but the action seems to unfold slower than expected.</Body>\n" +
            "        </Block>\n" +
            "    </Chapter>\n" +
            "</Book>\n";

    private static final String SAMPLE_CHAPTER_PATH = AnnotationEngineTest.class.getResource("/study-in-scarlet-chapter1.txt").getPath();

    @Test
    public void testAnnotateBook() throws JAXBException {
        // given
        final BookProcessingStatusListener mockListener = mock(BookProcessingStatusListener.class);
        final Book annotatedBook = AnnotationEngine.annotateBook(new StringReader(INPUT_STORYDOM),
                mockListener);

        // when
        final String outputStorydom = XmlWriter.exportBookToString(annotatedBook);

        // then
        assertEquals(EXPECTED_ANNOTATED_STORYDOM, outputStorydom);
        verify(mockListener, times(3)).updateProcessingStatus(anyDouble(), anyInt());
    }

    @Test
    void inspectChapterSentimentScore() throws IOException {
        // given
        final Chapter sampleChapter = createSampleChapter(FileUtils.readStringFromUri(SAMPLE_CHAPTER_PATH), "2776");
        final BigDecimal expectedSentiment = BigDecimal.valueOf(-0.6200203045685279);

        // when
        AnnotationEngine.inspectSentiments(sampleChapter);
        final BigDecimal sentimentScore = sampleChapter.getBlocks().get(0).getSentimentScore();

        // then
        assertEquals(expectedSentiment, sentimentScore);
    }

    private Chapter createSampleChapter(final String body, final String wordCount) {
        final Chapter sampleChapter = new Chapter();
        sampleChapter.setId("1");
        sampleChapter.getBlocks().add(new Block());
        sampleChapter.setTitle("Sample Chapter");
        sampleChapter.getBlocks().get(0).setBody(body);
        sampleChapter.getBlocks().get(0).setWordCount(wordCount);
        return sampleChapter;
    }

}