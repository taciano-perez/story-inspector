package com.o3.storyinspector.annotation.sentiments;

import com.o3.storyinspector.annotation.util.StanfordCoreNLPUtils;
import com.o3.storyinspector.annotation.wordcount.WordCountInspector;
import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Chapter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SentimentInspector {

    private final static DecimalFormat FORMATTER = new DecimalFormat("#.####");

    /**
     * Returns the sentiment score for a text
     *
     * @param text the text
     * @return the sentiment score
     */
    public static int inspectSentimentScore(final String text) {
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
    }

    /**
     * Given a chapter, breaks its text into blocks of approximately wordsPerBlock
     * (avoiding cutting sentences in the middle) and performs sentiment analysis on each block.
     *
     * @param chapter       the chapter to be analyzed
     * @param wordsPerBlock desired number of words per block
     * @return a list of new blocks with sentiment score and word count
     */
    public static List<Block> inspectSentimentScore(final Chapter chapter, final int wordsPerBlock) {
        final List<Block> newBlocks = new ArrayList<>();
        for (final Block oldBlock : chapter.getBlocks()) {
            final String body = oldBlock.getBody();
            final DocumentPreprocessor dp = new DocumentPreprocessor(new StringReader(body));
            StringBuilder buffer = new StringBuilder();
            int blockWordCount = 0;
            double accumulatedSentimentScore = 0;
            int sentenceCount = 0;
            for (List<HasWord> sentence : dp) {
                String sentenceString = SentenceUtils.listToString(sentence);
                blockWordCount += WordCountInspector.inspectWordCount(sentenceString);
                sentenceCount++;
                accumulatedSentimentScore += inspectSentimentScore(sentenceString);
                buffer.append(sentenceString);
                if (blockWordCount >= wordsPerBlock) {
                    final double weight = blockWordCount / wordsPerBlock;
                    final double sentimentScore = (accumulatedSentimentScore / sentenceCount) * weight;
                    newBlocks.add(newBlock(buffer.toString(), blockWordCount, sentimentScore));
                    buffer = new StringBuilder();
                    blockWordCount = 0;
                    accumulatedSentimentScore = 0;
                    sentenceCount = 0;
                }
            }
        }
        return newBlocks;
    }

    private static Block newBlock(final String body, final int wordCount, final double sentimentScore) {
        // TODO: add block unique id for cross-reference
        final Block block = new Block();
        block.setBody(body);
        block.setSentimentScore(String.format("%.4f", sentimentScore));
        block.setWordCount(Integer.toString(wordCount));
        return block;
    }

}