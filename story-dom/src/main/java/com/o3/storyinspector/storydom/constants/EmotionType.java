package com.o3.storyinspector.storydom.constants;

import java.security.InvalidParameterException;

public enum EmotionType {
    ANGER("anger"),
    ANTICIPATION("anticipation"),
    DISGUST("disgust"),
    FEAR("fear"),
    SADNESS("sadness"),
    SURPRISE("surprise"),
    TRUST("trust");

    private String emotionString;

    public static EmotionType emotionTypeFor(final String name) {
        if (EmotionType.ANGER.asString().equals(name.toLowerCase())) {
            return EmotionType.ANGER;
        }
        if (EmotionType.ANTICIPATION.asString().equals(name.toLowerCase())) {
            return EmotionType.ANTICIPATION;
        }
        if (EmotionType.DISGUST.asString().equals(name.toLowerCase())) {
            return EmotionType.DISGUST;
        }
        if (EmotionType.FEAR.asString().equals(name.toLowerCase())) {
            return EmotionType.FEAR;
        }
        if (EmotionType.SADNESS.asString().equals(name.toLowerCase())) {
            return EmotionType.SADNESS;
        }
        if (EmotionType.SURPRISE.asString().equals(name.toLowerCase())) {
            return EmotionType.SURPRISE;
        }
        if (EmotionType.TRUST.asString().equals(name.toLowerCase())) {
            return EmotionType.TRUST;
        }
        throw new InvalidParameterException("unknown emotion type");
    }

    EmotionType(final String emotion) {
        this.emotionString = emotion;
    }

    public String asString() {
        return emotionString;
    }
}
