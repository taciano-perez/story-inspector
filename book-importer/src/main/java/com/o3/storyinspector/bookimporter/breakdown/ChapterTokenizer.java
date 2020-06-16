package com.o3.storyinspector.bookimporter.breakdown;

import com.o3.storyinspector.storydom.Chapter;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;

import java.util.ArrayList;
import java.util.List;

public class ChapterTokenizer {

    public static List<Chapter> tokenize(final String text) {
        DocumentPreprocessor dp = new DocumentPreprocessor(text);
        List<Chapter> chapters = new ArrayList<>();
        Chapter currentChapter = null;
        int chapterNum = 0;
        for (List<HasWord> sentence : dp) {
            String sentenceString = SentenceUtils.listToString(sentence);
            //sentenceString = sentenceString.substring(1, sentenceString.length() - 1); // remove trailing []s
            if (onlyLetters(sentenceString).toLowerCase().startsWith("chapter")) {
                currentChapter = new Chapter();
                chapters.add(currentChapter);
                currentChapter.setTitle("Chapter " + ++chapterNum);
                currentChapter.setBody("");
            } else {
                if (currentChapter != null) {
                    currentChapter.setBody(currentChapter.getBody().concat(sentenceString));
                }
            }
        }
        return chapters;
    }

    private static String onlyLetters(final String input) {
        return input.replaceAll("\\P{L}+", "");
    }

}
