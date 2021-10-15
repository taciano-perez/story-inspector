package com.o3.storyinspector.bookimporter.breakdown;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Chapter;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ChapterTokenizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChapterTokenizer.class);

    public static List<Chapter> tokenizeFromFile(final String inputFilePath) {
        LOGGER.info("Preparing to tokenize file: " + inputFilePath);
        try {
            return tokenizeFromReader(new FileReader(inputFilePath));
        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.error(fileNotFoundException.getMessage());
            fileNotFoundException.printStackTrace();
            return null;
        }
    }

    public static List<Chapter> tokenizeFromReader(final Reader inputBookReader) {
        return tokenize(new DocumentPreprocessor(inputBookReader));
    }

    private static List<Chapter> tokenize(final DocumentPreprocessor dp) {
        final List<Chapter> chapters = new ArrayList<>();
        Chapter currentChapter = null;
        int uniqueChapterId = 1;
        for (final List<HasWord> sentence : dp) {
            final String sentenceString = SentenceUtils.listToOriginalTextString(sentence)
                    .replaceAll("[\\n\\r]", " ")    // remove new lines
                    .replaceAll("\\s+", " ")        // remove multiple spacing
                    .trim();                                       // remove leading & trailing spaces
            if (onlyLetters(sentenceString).toLowerCase().startsWith("chapter")) {
                currentChapter = new Chapter();
                currentChapter.setId(Integer.toString(uniqueChapterId++));
                currentChapter.getBlocks().add(new Block());
                chapters.add(currentChapter);
                currentChapter.setTitle(cleanChapterName(sentenceString));
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

    private static String cleanChapterName(final String inputChapterName) {
        String outputChapterName = inputChapterName;

        // remove trailing characters before "chapter"
        while (!outputChapterName.toLowerCase().startsWith("chapter")) {
            outputChapterName = outputChapterName.substring(1);
            if (outputChapterName.isEmpty()) {  // should never happen, but just in case...
                outputChapterName = inputChapterName;
                break;
            }
        }

        return outputChapterName.trim();
    }

    private static boolean isBlankString(final String string) {
        return string == null || string.trim().isEmpty();
    }

    private static String onlyLetters(final String input) {
        return input.replaceAll("\\P{L}+", "");
    }

}
