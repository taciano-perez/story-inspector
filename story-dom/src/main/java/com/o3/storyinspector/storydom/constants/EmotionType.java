package com.o3.storyinspector.storydom.constants;

public enum EmotionType {
    ANGER("anger"),
    ANTICIPATION("anticipation"),
    DISGUST("disgust"),
    FEAR("fear"),
    SADNESS("sadness"),
    SURPRISE("surprise"),
    TRUST("trust");

    private String emotionString;

    EmotionType(final String emotion) {
        this.emotionString = emotion;
    }

    public String asString() {
        return emotionString;
    }
}
