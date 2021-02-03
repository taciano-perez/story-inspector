package com.o3.storyinspector.annotation.util;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StanfordCoreNLPUtils {

    private static final Logger LOG = LoggerFactory.getLogger(StanfordCoreNLPUtils.class);

    private static final String numberOfCores = "2";

    private static StanfordCoreNLP pipelineSingleton = null;

    private static void init() {
        final Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment, pos, lemma, ner");
        props.setProperty("depparse.nthreads", numberOfCores);
        props.setProperty("parse.nthreads", numberOfCores);
        // ner (named entity recognition) properties (see https://stanfordnlp.github.io/CoreNLP/ner.html)
        // apply to both locations and characters
        props.setProperty("ner.nthreads", numberOfCores);
        props.setProperty("ner.applyNumericClassifiers", "false");  // omit money, percent, numbers, time
        props.setProperty("ner.applyFineGrained", "false");  // don't apply fine-grained tags (e.g. LOCATION –> CITY)
        props.setProperty("ner.useSUTime", "false");  // omit SUTime
        pipelineSingleton = new StanfordCoreNLP(props);
    }

    public static synchronized StanfordCoreNLP getPipelineInstance() {
        if (pipelineSingleton == null) {
            init();
        }
        return pipelineSingleton;
    }

    public static List<String> splitSentences(final String inputText) {
        // Tokenize
        final List<CoreLabel> tokens = new ArrayList<>();
        final PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(new StringReader(inputText),
                new CoreLabelTokenFactory(), "invertible=true,tokenizeNLs=true");
        while (tokenizer.hasNext()) {
            tokens.add(tokenizer.next());
        }
        // Split sentences from tokens
        final List<List<CoreLabel>> sentences = new WordToSentenceProcessor<CoreLabel>().process(tokens);
        // Join back together
        int end;
        int start = 0;
        final List<String> sentenceList = new ArrayList<>();
        for (List<CoreLabel> sentence : sentences) {
            end = sentence.get(sentence.size() - 1).endPosition();
            sentenceList.add(inputText.substring(start, end).trim());
            start = end;
        }
        return sentenceList;
    }

    public static List<NamedEntityToken> extractNamedEntities(String text) {
        final StanfordCoreNLP pipeline = StanfordCoreNLPUtils.getPipelineInstance();

        final List<NamedEntityToken> tokens = new ArrayList<>();

        // code below from https://www.informit.com/articles/article.aspx?p=2265404
        // run all Annotators on the passed-in text
        final Annotation document = new Annotation(text);
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
            boolean newToken = true;
            for (final CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String currNeToken = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
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
                    sb.append(" ").append(word);
                } else {
                    // We're done with the current entity - handle it and reset
                    handleEntity(prevNeToken, sb, tokens);
                    newToken = true;
                }
                prevNeToken = currNeToken;
            }
        }

        return tokens;
    }

    private static void handleEntity(String inKey, StringBuilder inSb, List<NamedEntityToken> inTokens) {
        final NamedEntityToken token = new NamedEntityToken(inKey, inSb.toString());
        LOG.debug("'{}' is a {}", token.getName(), token.getType());
        inTokens.add(token);
        inSb.setLength(0);
    }
}
