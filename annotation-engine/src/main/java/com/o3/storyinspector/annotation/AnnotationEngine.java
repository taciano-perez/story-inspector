package com.o3.storyinspector.annotation;

import com.o3.storyinspector.annotation.blocks.BlockSplitter;
import com.o3.storyinspector.annotation.emotions.EmotionInspector;
import com.o3.storyinspector.annotation.locations.NamedEntities;
import com.o3.storyinspector.annotation.locations.NamedEntitiesInspector;
import com.o3.storyinspector.annotation.sentiments.SentimentInspector;
import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.*;
import com.o3.storyinspector.storydom.constants.EmotionType;
import com.o3.storyinspector.storydom.io.XmlReader;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Takes a StoryDOM as input and annotates it with metadata
 * (locations, characters, sentiment score, word count).
 */
public class AnnotationEngine {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationEngine.class);

    public static final int NR_WORDS_PER_BLOCK = 250;

    /**
     * Annotates a book from a file in a local directory.
     *
     * @param inputBookPath           the path to the local directory.
     * @param outputAnnotatedBookPath the path to the annotated book
     * @throws IOException   when we cannot read/write from/to the file
     * @throws JAXBException when we cannot read/write XML
     */
    public static void annotateBook(final String inputBookPath, final String outputAnnotatedBookPath) throws IOException, JAXBException {
        final Book book = XmlReader.readBookFromXmlFile(inputBookPath);

        for (Chapter chapter : book.getChapters()) {
            // TODO: refactor this so we don't mutate the DOM objects
            AnnotationEngine.annotateChapter(chapter);
            XmlWriter.exportBookToXmlFile(book, new File(outputAnnotatedBookPath)); // flush every chapter
        }
    }

    /**
     * Annotates a book from an input stream reader.
     *
     * @param inputBookReader the input stream book reader
     * @return the annotated book
     * @throws JAXBException when we cannot read/write XML
     */
    public static Book annotateBook(final Reader inputBookReader) throws JAXBException {
        final Book book = XmlReader.readBookFromXmlStream(inputBookReader);

        for (Chapter chapter : book.getChapters()) {
            // TODO: refactor this so we don't mutate the DOM objects
            AnnotationEngine.annotateChapter(chapter);
        }

        return book;
    }

    private static void annotateChapter(final Chapter chapter) {
        if (chapter.getMetadata() == null) {
            chapter.setMetadata(new Metadata());
            chapter.getMetadata().setLocations(new Locations());
            chapter.getMetadata().setCharacters(new Characters());
        }
        long time;

        time = logStart("Chapter: [" + chapter.getTitle() + "]. COUNTING WORDS...");
        countWords(chapter);
        logEnd(time);

        time = logStart("Chapter: [" + chapter.getTitle() + "]. BREAKING DOWN BLOCKS...");
        breakDownBlocks(chapter);
        logEnd(time);

        time = logStart("Chapter: [" + chapter.getTitle() + "]. INSPECTING SENTIMENTS...");
        inspectSentiments(chapter);
        logEnd(time);

        time = logStart("Chapter: [" + chapter.getTitle() + "]. INSPECTING EMOTIONS...");
        inspectEmotions(chapter);
        logEnd(time);

        time = logStart("Chapter: [" + chapter.getTitle() + "]. INSPECTING NAMED ENTITIES...");
        inspectNamedEntities(chapter);
        logEnd(time);

        LOG.info("Chapter: [" + chapter.getTitle() + "] INSPECTION COMPLETE.");
    }

    private static long logStart() {
        return System.currentTimeMillis();
    }

    private static long logStart(final String message) {
        LOG.info(message);
        return logStart();
    }

    private static void logEnd(final long start) {
        final long end = System.currentTimeMillis();
        LOG.debug("Time elapsed: " + ((end - start) / 1000) + " secs.");
    }

    private static void breakDownBlocks(final Chapter chapter) {
        final List<Block> newBlocks = BlockSplitter.splitChapter(chapter, NR_WORDS_PER_BLOCK);
        chapter.getBlocks().clear();
        chapter.getBlocks().addAll(newBlocks);
    }

    private static void inspectNamedEntities(final Chapter chapter) {
        try {
            final NamedEntities namedEntities = NamedEntitiesInspector.inspectNamedEntities(getChapterBody(chapter));
            chapter.getMetadata().getLocations().getLocations().addAll(namedEntities.getLocations());
            chapter.getMetadata().getCharacters().getCharacters().addAll(namedEntities.getCharacters());
        } catch (IOException ioe) {
            LOG.error("Error while inspecting named entities on chapter " + chapter.getTitle() + " .");
            ioe.printStackTrace(System.err);
        }
    }

    static void inspectSentiments(final Chapter chapter) {
        for (final Block block : chapter.getBlocks()) {
            block.setSentimentScore(Double.toString(SentimentInspector.inspectSentimentScore(block, NR_WORDS_PER_BLOCK)));
        }
    }

    private static void inspectEmotions(final Chapter chapter) {
        // assume blocks of NR_WORDS_PER_BLOCK
        for (final Block block : chapter.getBlocks()) {
            LOG.debug("Inspecting emotion of block [" + block.getBody() + "]");
            for (EmotionType emotion : EmotionType.values()) {
                LOG.debug("Emotion: " + emotion.asString());
                final Emotion emotionBlock = new Emotion();
                emotionBlock.setType(emotion.asString());
                try {
                    final double score = EmotionInspector.inspectEmotionScore(emotion, block.getBody());
                    emotionBlock.setScore(BigDecimal.valueOf(score));
                } catch (final Throwable t) {
                    LOG.error("Ignoring error: " + t.getLocalizedMessage());
                    t.printStackTrace();
                    emotionBlock.setScore(BigDecimal.ZERO);
                }
                block.getEmotions().add(emotionBlock);
            }
        }
    }

    private static void countWords(final Chapter chapter) {
        final int wordCount = WordCountInspector.inspectWordCount(getChapterBody(chapter));
        chapter.getMetadata().setWordCount(Integer.toString(wordCount));
    }

    /**
     * Reads all chapter body regardless of blocks.
     *
     * @return complete chapter body
     */
    private static String getChapterBody(final Chapter chapter) {
        return chapter.getBlocks().stream()
                .map(Block::getBody)
                .collect(Collectors.joining());
    }
}