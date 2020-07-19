package com.o3.storyinspector.annotation.emotions;

import com.o3.storyinspector.annotation.emotions.emolex.EmoLex;
import com.o3.storyinspector.storydom.constants.EmotionType;

import java.util.StringTokenizer;

public class EmotionInspector {

    public static double inspectEmotionScore(final EmotionType emotion, final String text) {
        final StringTokenizer tokenizer = new StringTokenizer(text, " \t\n\r\f,.:;?![]'\"(){}-");
        int numOfWords = 0;
        double accumulatedScore = 0;
        while (tokenizer.hasMoreTokens()) {
            final String word = tokenizer.nextToken();
            numOfWords++;
            accumulatedScore += EmoLex.getInstance().getEmotionScore(emotion, word);
        }
        if (accumulatedScore < 1) {
            return 0;
        } else {
            return accumulatedScore / numOfWords;
        }
    }
}
