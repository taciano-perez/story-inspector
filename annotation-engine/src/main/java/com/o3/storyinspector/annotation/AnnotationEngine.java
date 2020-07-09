package com.o3.storyinspector.annotation;

import com.o3.storyinspector.annotation.characters.CharacterInspector;
import com.o3.storyinspector.annotation.emotions.EmotionInspector;
import com.o3.storyinspector.annotation.locations.LocationInspector;
import com.o3.storyinspector.annotation.sentiments.SentimentInspector;
import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.*;
import com.o3.storyinspector.storydom.constants.EmotionType;
import com.o3.storyinspector.storydom.io.XmlReader;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Takes a StoryDOM as input and annotates it with metadata
 * (locations, characters, sentiment score, word count).
 */
public class AnnotationEngine {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationEngine.class);

    private static final int NR_WORDS_PER_BLOCK = 250;

    public static void annotateBook(final String inputBookPath, final String outputAnnotatedBookPath) throws IOException, JAXBException {
        final Book book = XmlReader.readBookFromXmlFile(inputBookPath);

        for (Chapter chapter : book.getChapters()) {
            // TODO: refactor this so we don't mutate the DOM objects
            AnnotationEngine.annotateChapter(chapter);
            XmlWriter.exportBookToXmlFile(book, new File(outputAnnotatedBookPath)); // flush every chapter
        }
    }

    private static void annotateChapter(final Chapter chapter) {
        if (chapter.getMetadata() == null) {
            chapter.setMetadata(new Metadata());
            chapter.getMetadata().setLocations(new Locations());
            chapter.getMetadata().setCharacters(new Characters());
        }
        long time;

        time = logStart("Chapter: " + chapter.getTitle() + " . COUNTING WORDS...");
        countWords(chapter);
        logEnd(time);

        time = logStart("Chapter: " + chapter.getTitle() + " . INSPECTING SENTIMENTS...");
        inspectSentiments(chapter);
        logEnd(time);

        time = logStart("Chapter: " + chapter.getTitle() + " . INSPECTING EMOTIONS...");
        inspectEmotions(chapter);
        logEnd(time);

//        time = logStart("Chapter: " + chapter.getTitle() + " . INSPECTING CHARACTERS...");
//        inspectCharacters(chapter);
//        logEnd(time);
//
//        time = logStart("Chapter: " + chapter.getTitle() + " . INSPECTING LOCATIONS...");
//        inspectLocations(chapter);
//        logEnd(time);

        LOG.info("Chapter: " + chapter.getTitle() + " INSPECTION COMPLETE.");
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

    private static void inspectCharacters(final Chapter chapter) {
        try {
            final Set<String> characterNames = CharacterInspector.inspectNamedCharacters(getChapterBody(chapter));
            final Set<Character> characters = characterNames.stream().map(AnnotationEngine::buildCharacter).collect(Collectors.toSet());
            chapter.getMetadata().getCharacters().getCharacters().addAll(characters);
        } catch (IOException ioe) {
            LOG.error("Error while inspecting characters on chapter " + chapter.getTitle() + " .");
            ioe.printStackTrace(System.err);
        }
    }

    private static Character buildCharacter(final String name) {
        final Character character = new Character();
        character.setName(name);
        return character;
    }

    private static void inspectLocations(final Chapter chapter) {
        try {
            Set<Location> locations = LocationInspector.inspectNamedLocations(getChapterBody(chapter));
            chapter.getMetadata().getLocations().getLocations().addAll(locations);
        } catch (IOException ioe) {
            LOG.error("Error while inspecting locations on chapter " + chapter.getTitle() + " .");
            ioe.printStackTrace(System.err);
        }
    }

    private static void inspectSentiments(final Chapter chapter) {
        final List<Block> newBlocks = SentimentInspector.inspectSentimentScore(chapter, NR_WORDS_PER_BLOCK);
        chapter.getBlocks().clear();
        chapter.getBlocks().addAll(newBlocks);
    }

    private static void inspectEmotions(final Chapter chapter) {
        // assume blocks of NR_WORDS_PER_BLOCK
        for (final Block block : chapter.getBlocks()) {
            for (EmotionType emotion : EmotionType.values()) {
                final double score = EmotionInspector.inspectEmotionScore(emotion, block.getBody());
                final Emotion emotionBlock = new Emotion();
                emotionBlock.setType(emotion.asString());
                emotionBlock.setScore(BigDecimal.valueOf(score));
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