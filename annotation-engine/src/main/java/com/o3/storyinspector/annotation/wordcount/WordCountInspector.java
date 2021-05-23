package com.o3.storyinspector.annotation.wordcount;

import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Chapter;

import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class WordCountInspector {

    public static int inspectWordCount(final String text) {
        final StringTokenizer tokenizer = new StringTokenizer(text);
        return tokenizer.countTokens();
    }

    public static int inspectChapterWordCount(final Chapter chapter) {
        return WordCountInspector.inspectWordCount(getChapterBody(chapter));
    }

    private static String getChapterBody(final Chapter chapter) {
        return chapter.getBlocks().stream()
                .map(Block::getBody)
                .collect(Collectors.joining());
    }

}
