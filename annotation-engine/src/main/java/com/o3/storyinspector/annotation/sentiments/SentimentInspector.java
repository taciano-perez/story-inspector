package com.o3.storyinspector.annotation.sentiments;

import com.o3.storyinspector.annotation.util.StanfordCoreNLPSingleton;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.ejml.simple.SimpleMatrix;

public class SentimentInspector {

    public static int inspectSentimentScore(final String text) throws Exception {
        int mainSentiment = 0;
        if (text != null && text.length() > 0) {
            int longest = 0;
            final Annotation annotation = StanfordCoreNLPSingleton.getInstance().process(text);
            for (CoreMap sentence : annotation
                    .get(CoreAnnotations.SentencesAnnotation.class)) {
                final Tree tree = sentence
                        .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                final int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                final SimpleMatrix sentiment_new = RNNCoreAnnotations.getPredictions(tree); // why?
                final String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }
            }
        }
        return mainSentiment;
    }
}