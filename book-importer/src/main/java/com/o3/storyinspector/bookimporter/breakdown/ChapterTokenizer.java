package com.o3.storyinspector.bookimporter.breakdown;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Chapter;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ChapterTokenizer {

    public static List<Chapter> tokenizeFromFile(final String inputFilePath) {
        return tokenize(new DocumentPreprocessor(inputFilePath));
    }

    public static List<Chapter> tokenizeFromReader(final Reader inputBookReader) {
        return tokenize(new DocumentPreprocessor(inputBookReader));
    }

    private static List<Chapter> tokenize(final DocumentPreprocessor dp) {
        final List<Chapter> chapters = new ArrayList<>();
        Chapter currentChapter = null;
        for (final List<HasWord> sentence : dp) {
            final String sentenceString = SentenceUtils.listToOriginalTextString(sentence)
                    .replaceAll("[\\n\\r]", " ")    // remove new lines
                    .replaceAll("\\s+", " ")        // remove multiple spacing
                    .trim();                                       // remove leading & trailing spaces
            if (onlyLetters(sentenceString).toLowerCase().startsWith("chapter")) {
                currentChapter = new Chapter();
                currentChapter.getBlocks().add(new Block());
                chapters.add(currentChapter);
                currentChapter.setTitle(sentenceString);
                currentChapter.getBlocks().get(0).setBody("");
            } else {
                if (currentChapter != null && currentChapter.getBlocks().get(0) != null) {
                    final Block bodyBlock = currentChapter.getBlocks().get(0);
                    if (!isBlankString(bodyBlock.getBody())) {
                        // add a space between sentences
                        bodyBlock.setBody(bodyBlock.getBody().concat(" "));
                    }
                    bodyBlock.setBody(bodyBlock.getBody().concat(sentenceString));
                }
            }
        }
        return chapters;
    }

    private static boolean isBlankString(final String string) {
        return string == null || string.trim().isEmpty();
    }

    private static String onlyLetters(final String input) {
        return input.replaceAll("\\P{L}+", "");
    }

}
