package com.o3.storyinspector.annotation;

import com.o3.storyinspector.annotation.characters.CharacterInspector;
import com.o3.storyinspector.annotation.locations.LocationInspector;
import com.o3.storyinspector.annotation.sentiments.SentimentInspector;
import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.*;
import com.o3.storyinspector.storydom.io.XmlReader;
import com.o3.storyinspector.storydom.io.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationEngine {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationEngine.class);

    public static void annotateBook(final String inputBookPath, final String outputAnnotatedBookPath) throws IOException, JAXBException {
        final Book book = XmlReader.readBookFromXmlFile(inputBookPath);

        // TODO: refactor this so we don't mutate the DOM objects
        book.getChapters().forEach(AnnotationEngine::annotateChapter);

        XmlWriter.exportBookToXmlFile(book, new File(outputAnnotatedBookPath));
    }

    private static void annotateChapter(final Chapter chapter) {
        if (chapter.getMetadata() == null) {
            chapter.setMetadata(new Metadata());
            chapter.getMetadata().setLocations(new Locations());
            chapter.getMetadata().setCharacters(new Characters());
        }
        LOG.info("Chapter: " + chapter.getTitle() + " . COUNTING WORDS...");
        countWords(chapter);
        LOG.info("Chapter: " + chapter.getTitle() + " . INSPECTING SENTIMENTS...");
        inspectSentiments(chapter);
        LOG.info("Chapter: " + chapter.getTitle() + " . INSPECTING CHARACTERS...");
        inspectCharacters(chapter);
        LOG.info("Chapter: " + chapter.getTitle() + " . INSPECTING LOCATIONS...");
        inspectLocations(chapter);
        LOG.info("Chapter: " + chapter.getTitle() + " INSPECTION COMPLETE.");
    }

    private static void inspectCharacters(final Chapter chapter) {
        try {
            final Set<String> characterNames = CharacterInspector.inspectNamedCharacters(chapter.getBody());
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
            Set<Location> locations = LocationInspector.inspectNamedLocations(chapter.getBody());
            chapter.getMetadata().getLocations().getLocations().addAll(locations);
        } catch (IOException ioe) {
            LOG.error("Error while inspecting locations on chapter " + chapter.getTitle() + " .");
            ioe.printStackTrace(System.err);
        }
    }

    private static void inspectSentiments(final Chapter chapter) {
        final int sentimentScore = SentimentInspector.inspectSentimentScore(chapter.getBody());
        chapter.getMetadata().setSentimentScore(Integer.toString(sentimentScore));
    }

    private static void countWords(final Chapter chapter) {
        final int wordCount = WordCountInspector.inspectWordCount(chapter.getBody());
        chapter.getMetadata().setWordCount(Integer.toString(wordCount));
    }

}
