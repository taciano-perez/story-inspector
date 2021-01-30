package com.o3.storyinspector.annotation.sentiments;

import com.o3.storyinspector.annotation.util.StanfordCoreNLPUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SentimentInspector {

    private static final Logger LOG = LoggerFactory.getLogger(SentimentInspector.class);

    /**
     * Returns the sentiment score for a text
     *
     * @param text the text
     * @return the sentiment score
     */
    public static int inspectSentimentScore(final String text) {
        try {
            int mainSentiment = 0;
            if (text != null && text.length() > 0) {
                int longest = 0;
                final Annotation annotation = StanfordCoreNLPUtils.getPipelineInstance().process(text);
                for (CoreMap sentence : annotation
                        .get(CoreAnnotations.SentencesAnnotation.class)) {
                    final Tree tree = sentence
                            .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                    final int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                    final String partText = sentence.toString();
                    if (partText.length() > longest) {
                        mainSentiment = sentiment;
                        longest = partText.length();
                    }
                }
            }
            return mainSentiment;
        } catch (final Throwable t) {
            LOG.error("Error while computing sentiment score, will skip scoring this block. Error: " + t.getLocalizedMessage());
            LOG.debug("Block that resulted in error: [" + text + "]");
            t.printStackTrace();
            return 0;
        }
    }
}