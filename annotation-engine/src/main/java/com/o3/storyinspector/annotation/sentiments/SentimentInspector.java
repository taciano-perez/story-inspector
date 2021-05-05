package com.o3.storyinspector.annotation.sentiments;

import com.o3.storyinspector.annotation.AnnotationEngine;
import com.o3.storyinspector.annotation.blocks.SentenceSplitter;
import com.o3.storyinspector.annotation.util.StanfordCoreNLPUtils;
import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.Block;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SentimentInspector {

    private static final Logger LOG = LoggerFactory.getLogger(SentimentInspector.class);

    /**
     * Returns the sentiment score for a block.
     *
     * @param block the block
     * @return the sentiment score
     */
    public static double inspectSentimentScore(final Block block, final int wordsPerBlock) {
        LOG.debug("Inspecting sentiment on block: [" + block.getBody() + "]");
        final List<String> sentences = SentenceSplitter.splitSentences(block);
        double accumulatedSentimentScore = 0;
        int numOfSentences = 0;
        for (final String sentence : sentences) {
            final int wordCount = WordCountInspector.inspectWordCount(sentence);
            if (wordCount <= AnnotationEngine.MAX_SENTENCE_LENGTH) {
                accumulatedSentimentScore += SentimentInspector.inspectSentimentScore(sentence);
            } else {
                // score = 0
                LOG.warn("Sentence too long (" + wordCount + " words), skipping sentiment analysis.");
            }
            numOfSentences++;
        }
        final double blockWeight = Double.parseDouble(block.getWordCount()) / wordsPerBlock;
        LOG.debug("weight: [" + blockWeight + "]");
        final double weightedSentimentScore = (accumulatedSentimentScore / numOfSentences) * blockWeight;
        LOG.debug("weightedScore: [" + weightedSentimentScore + "]");
        return weightedSentimentScore;
    }

    /**
     * Returns the sentiment score for a text
     *
     * @param text the text
     * @return the sentiment score
     */
    static int inspectSentimentScore(final String text) {
        try {
            // from https://stackoverflow.com/questions/28014779/stanford-sentiment-analysis-score-java
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
            return mainSentiment - 2; // -2 for range -1...1
        } catch (final Exception e) {
            LOG.error("Error while computing sentiment score, will skip scoring this block. Error: " + e.getLocalizedMessage());
            LOG.error("Block that resulted in error: [" + text + "]");
            e.printStackTrace();
            return 0;
        }
    }
}