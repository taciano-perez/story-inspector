package com.o3.storyinspector.annotation.emotions.emolex;

import com.o3.storyinspector.storydom.constants.EmotionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmoLexTest {

    @Test
    void getInstance() {

        // when
        EmoLex emolex = EmoLex.getInstance();

        // then
        assertEquals(0.898, emolex.getEmotionScore(EmotionType.ANGER, "annihilate"));
    }
}