package com.o3.storyinspector.annotation.locations;

import com.o3.storyinspector.annotation.util.NamedEntityToken;
import com.o3.storyinspector.annotation.util.StanfordCoreNLPUtils;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.stanford.nlp.ie.KBPRelationExtractor.NERTag.*;

public class LocationInspector {

    private static boolean USE_STANFORD_CORE = true;


    public static Set<String> inspectNamedLocations(String text) throws Exception {
        if (USE_STANFORD_CORE) {
            return inspectUsingStanfordCoreNLP(text);
        } else {
            return inspectUsingOpenNLP(text);
        }
    }

    private static Set<String> inspectUsingStanfordCoreNLP(String text) {
        // TODO: this pass identifies other NEs, refactor to take that into account
        return StanfordCoreNLPUtils.extractNamedEntities(text).stream()
                .filter(token -> token.type().equals(LOCATION.name) ||
                        token.type().equals(COUNTRY.name) ||
                        token.type().equals(CITY.name))
                .map(NamedEntityToken::typeAndName)
                .collect(Collectors.toSet());
    }

    private static Set<String> inspectUsingOpenNLP(String text) throws Exception {
        // tokenize text
        final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        final String[] tokens = tokenizer.tokenize(text);

        // Apply OpenNLP NER (Named Entity Recognition) model
        final InputStream inputStreamNameFinder = LocationInspector.class
                .getResourceAsStream("/nlp-models/en-ner-location.bin");
        final TokenNameFinderModel model = new TokenNameFinderModel(
                inputStreamNameFinder);
        final NameFinderME nameFinderME = new NameFinderME(model);
        final List<Span> spans = Arrays.asList(nameFinderME.find(tokens));
        return spans.stream()
                .map(s -> tokens[s.getStart()])
                .collect(Collectors.toSet());
    }

}
