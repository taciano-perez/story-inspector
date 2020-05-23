package com.o3.storyinspector.annotation.locations;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LocationInspector {

    public static Set<String> inspectNamedLocations(String text) throws Exception {
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
        final Set<String> namedLocations = spans.stream()
                .map(s -> tokens[s.getStart()])
                .collect(Collectors.toSet());

        return namedLocations;
    }
}
