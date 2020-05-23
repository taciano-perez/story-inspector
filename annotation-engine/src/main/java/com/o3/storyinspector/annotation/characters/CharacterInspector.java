package com.o3.storyinspector.annotation.characters;

import com.o3.storyinspector.annotation.util.StanfordCoreNLPSingleton;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static edu.stanford.nlp.ie.KBPRelationExtractor.NERTag.PERSON;

/**
 * Identifies named characters in text fragments.
 */
public class CharacterInspector {

    private static final Logger LOG = LoggerFactory.getLogger(CharacterInspector.class);

    private static boolean USE_STANFORD_CORE = true;

    public static Set<String> inspectNamedCharacters(String text) throws Exception {
        if (USE_STANFORD_CORE) {
            return inspectUsingStanfordCoreNLP(text);
        } else {
            return inspectUsingOpenNLP(text);
        }
    }

    private static Set<String> inspectUsingStanfordCoreNLP(String s) throws Exception {
        final StanfordCoreNLP pipeline = StanfordCoreNLPSingleton.getInstance();

        final List<EmbeddedToken> tokens = new ArrayList<>();

        // example from https://www.informit.com/articles/article.aspx?p=2265404
        // run all Annotators on the passed-in text
        final Annotation document = new Annotation(s);
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with
        // custom types
        final List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        final StringBuilder sb = new StringBuilder();

        //I don't know why I can't get this code out of the box from StanfordNLP, multi-token entities
        //are far more interesting and useful..
        for (final CoreMap sentence : sentences) {
            // traversing the words in the current sentence, "O" is a sensible default to initialise
            // tokens to since we're not interested in unclassified / unknown things..
            String prevNeToken = "O";
            String currNeToken = "O";
            boolean newToken = true;
            for (final CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                currNeToken = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                final String word = token.get(CoreAnnotations.TextAnnotation.class);
                // Strip out "O"s completely, makes code below easier to understand
                if (currNeToken.equals("O")) {
                    // LOG.debug("Skipping '{}' classified as {}", word, currNeToken);
                    if (!prevNeToken.equals("O") && (sb.length() > 0)) {
                        handleEntity(prevNeToken, sb, tokens);
                        newToken = true;
                    }
                    continue;
                }

                if (newToken) {
                    prevNeToken = currNeToken;
                    newToken = false;
                    sb.append(word);
                    continue;
                }

                if (currNeToken.equals(prevNeToken)) {
                    sb.append(" " + word);
                } else {
                    // We're done with the current entity - print it out and reset
                    // TODO save this token into an appropriate ADT to return for useful processing..
                    handleEntity(prevNeToken, sb, tokens);
                    newToken = true;
                }
                prevNeToken = currNeToken;
            }
        }
        // TODO: this pass identifies other NEs, refactor to take that into account
        return tokens.stream()
                .filter(token -> token.getName().equals(PERSON.name))
                .map(EmbeddedToken::getValue)
                .collect(Collectors.toSet());
    }

    private static void handleEntity(String inKey, StringBuilder inSb, List inTokens) {
        final EmbeddedToken token = new EmbeddedToken(inKey, inSb.toString());
        //LOG.debug("'{}' is a {}", token.getValue(), token.getName());
        inTokens.add(token);
        inSb.setLength(0);
    }

    private static class EmbeddedToken {

        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public EmbeddedToken(String name, String value) {
            super();
            this.name = name;
            this.value = value;
        }
    }

    private static Set<String> inspectUsingOpenNLP(String text) throws Exception {
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
