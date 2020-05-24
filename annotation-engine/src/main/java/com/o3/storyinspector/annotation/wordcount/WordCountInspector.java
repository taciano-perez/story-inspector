package com.o3.storyinspector.annotation.wordcount;

import java.util.StringTokenizer;

public class WordCountInspector {

    public static int inspectWordCount(String text) {
        final StringTokenizer tokenizer = new StringTokenizer(text);
        return tokenizer.countTokens();
    }
}
