package com.o3.storyinspector.annotation.characters;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Identifies named characters in text fragments.
 */
public class CharacterInspector {

    public static Set<String> inspectNamedCharacters(String text) throws Exception {
        // tokenize text
        final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        final String[] tokens = tokenizer.tokenize(text);

        // Apply OpenNLP NER (Named Entity Recognition) model
        final InputStream inputStreamNameFinder = CharacterInspector.class
                .getResourceAsStream("/nlp-models/en-ner-person.bin");
        final TokenNameFinderModel model = new TokenNameFinderModel(
                inputStreamNameFinder);
        final NameFinderME nameFinderME = new NameFinderME(model);
        final List<Span> spans = Arrays.asList(nameFinderME.find(tokens));
        final Set<String> namedCharacters = spans.stream()
                .map(s -> tokens[s.getStart()])
                .collect(Collectors.toSet());

        return namedCharacters;
    }
}
