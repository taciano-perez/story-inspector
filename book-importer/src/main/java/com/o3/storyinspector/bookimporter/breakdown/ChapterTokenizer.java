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
        int chapterNum = 0;
        for (List<HasWord> sentence : dp) {
            // TODO: documentpreprocessor does not separate chapter title from body if there's no end punctuation.
            final String sentenceString = SentenceUtils.listToString(sentence);
            if (onlyLetters(sentenceString).toLowerCase().startsWith("chapter")) {
                currentChapter = new Chapter();
                currentChapter.getBlocks().add(new Block());
                chapters.add(currentChapter);
                currentChapter.setTitle("Chapter " + ++chapterNum);
                currentChapter.getBlocks().get(0).setBody("");
            } else {
                if (currentChapter != null && currentChapter.getBlocks().get(0) != null) {
                    currentChapter.getBlocks().get(0).setBody(currentChapter.getBlocks().get(0).getBody().concat(sentenceString));
                }
            }
        }
        return chapters;
    }

    private static String onlyLetters(final String input) {
        return input.replaceAll("\\P{L}+", "");
    }

}
