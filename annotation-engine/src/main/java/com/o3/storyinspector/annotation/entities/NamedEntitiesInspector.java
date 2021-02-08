package com.o3.storyinspector.annotation.entities;

import com.o3.storyinspector.annotation.AnnotationEngine;
import com.o3.storyinspector.annotation.util.NamedEntityToken;
import com.o3.storyinspector.annotation.util.StanfordCoreNLPUtils;
import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.Location;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.stanford.nlp.ie.KBPRelationExtractor.NERTag.*;

public class NamedEntitiesInspector {

    private static boolean USE_STANFORD_CORE = true;

    private static final Logger LOG = LoggerFactory.getLogger(NamedEntitiesInspector.class);

    /**
     * Returns the entities in a chapter.
     *
     * @param chapter the chapter
     * @return the named entities
     */
    public static NamedEntities inspectNamedEntities(final Chapter chapter) throws IOException {
        final NamedEntities namedEntities = new NamedEntities(new HashSet<>(), new HashSet<>());
        for (final Block block : chapter.getBlocks()) {
            LOG.debug("Inspecting NER on block: [" + block.getBody() + "]");
            final List<String> sentences = StanfordCoreNLPUtils.splitSentences(block.getBody());
            for (final String sentence : sentences) {
                final int wordCount = WordCountInspector.inspectWordCount(sentence);
                if (wordCount > AnnotationEngine.MAX_SENTENCE_LENGTH) {
                    LOG.warn("Sentence too long (" + wordCount + " words), skipping NER extraction.");
                } else {
                    final NamedEntities blockNamedEntities = inspectNamedEntities(sentence);
                    namedEntities.addAll(blockNamedEntities);
                }
            }
        }
        return namedEntities;
    }


    public static NamedEntities inspectNamedEntities(String text) throws IOException {
        if (USE_STANFORD_CORE) {
            return inspectNamedEntitiesUsingStanfordCoreNLP(text);
        } else {
            return new NamedEntities(inspectLocationsUsingOpenNLP(text), inspectCharactersUsingOpenNLP(text));
        }
    }

    private static NamedEntities inspectNamedEntitiesUsingStanfordCoreNLP(String text) {
        final List<NamedEntityToken> namedEntityTokens = StanfordCoreNLPUtils.extractNamedEntities(text);

        final Set<Location> locations = namedEntityTokens.stream()
                .filter(token -> token.getType().equals(LOCATION.name) ||
                        token.getType().equals(COUNTRY.name) ||
                        token.getType().equals(CITY.name))
                .map(token -> buildLocation(token.getName(), token.getType()))
                .collect(Collectors.toSet());

        final Set<String> characterNames = namedEntityTokens.stream()
                .filter(token -> token.getType().equals(PERSON.name))
                .map(NamedEntityToken::getName)
                .collect(Collectors.toSet());

        return new NamedEntities(locations, buildCharacters(characterNames));
    }

    private static Location buildLocation(final String name, final String type) {
        final Location location = new Location();
        location.setName(name);
        location.setType(type);
        return location;
    }

    private static Set<Location> inspectLocationsUsingOpenNLP(String text) throws IOException {
        // tokenize text
        final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        final String[] tokens = tokenizer.tokenize(text);

        // Apply OpenNLP NER (Named Entity Recognition) model
        final InputStream inputStreamNameFinder = NamedEntitiesInspector.class
                .getResourceAsStream("/nlp-models/en-ner-location.bin");
        final TokenNameFinderModel model = new TokenNameFinderModel(
                inputStreamNameFinder);
        final NameFinderME nameFinderME = new NameFinderME(model);
        final List<Span> spans = Arrays.asList(nameFinderME.find(tokens));
        return spans.stream()
                .map(s -> buildLocation(tokens[s.getStart()], "LOCATION"))
                .collect(Collectors.toSet());
    }

    private static Set<Character> inspectCharactersUsingOpenNLP(String text) throws IOException {
        // tokenize text
        final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        final String[] tokens = tokenizer.tokenize(text);

        // Apply OpenNLP NER (Named Entity Recognition) model
        final InputStream inputStreamNameFinder = NamedEntitiesInspector.class
                .getResourceAsStream("/nlp-models/en-ner-person.bin");
        final TokenNameFinderModel model = new TokenNameFinderModel(
                inputStreamNameFinder);
        final NameFinderME nameFinderME = new NameFinderME(model);
        final List<Span> spans = Arrays.asList(nameFinderME.find(tokens));
        return buildCharacters(spans.stream()
                .map(s -> tokens[s.getStart()])
                .collect(Collectors.toSet()));
    }

    private static Set<Character> buildCharacters(final Set<String> characterNames) {
        return characterNames.stream().map(NamedEntitiesInspector::buildCharacter).collect(Collectors.toSet());
    }

    private static Character buildCharacter(final String name) {
        final Character character = new Character();
        character.setName(name);
        return character;
    }

}
