package com.o3.storyinspector.viztool.sentiment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SentimentColorTest {

    @Test
    void getSentimentColorCode() {
        assertEquals("#00ff00", SentimentColor.getSentimentColorCode(1));
        assertEquals("#7fff00", SentimentColor.getSentimentColorCode(0.5));
        assertEquals("#ffff00", SentimentColor.getSentimentColorCode(0));
        assertEquals("#ff7f00", SentimentColor.getSentimentColorCode(-0.5));
        assertEquals("#ff0000", SentimentColor.getSentimentColorCode(-1));
    }
}