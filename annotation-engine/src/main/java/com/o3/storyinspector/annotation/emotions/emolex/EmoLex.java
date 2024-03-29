package com.o3.storyinspector.annotation.emotions.emolex;

import com.o3.storyinspector.storydom.constants.EmotionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class implementing emotion analysis functions using the NRC Word-Emotion Association Lexicon (EmoLex).
 */
public class EmoLex {

    private static final Logger LOG = LoggerFactory.getLogger(EmoLex.class);

    private static EmoLex singleton;

    public static synchronized EmoLex getInstance() {
        if (singleton == null) {
            singleton = new EmoLex();
        }
        return singleton;
    }

    private static final Map<EmotionType, String> SCORES_FILENAME_PER_EMOTION = new HashMap<>();

    static {
        SCORES_FILENAME_PER_EMOTION.put(EmotionType.ANGER, "emolex/anger-scores.txt");
        SCORES_FILENAME_PER_EMOTION.put(EmotionType.ANTICIPATION, "emolex/anticipation-scores.txt");
        SCORES_FILENAME_PER_EMOTION.put(EmotionType.DISGUST, "emolex/disgust-scores.txt");
        SCORES_FILENAME_PER_EMOTION.put(EmotionType.FEAR, "emolex/fear-scores.txt");
        SCORES_FILENAME_PER_EMOTION.put(EmotionType.SADNESS, "emolex/sadness-scores.txt");
        SCORES_FILENAME_PER_EMOTION.put(EmotionType.SURPRISE, "emolex/surprise-scores.txt");
        SCORES_FILENAME_PER_EMOTION.put(EmotionType.TRUST, "emolex/trust-scores.txt");
    }

    private Map<EmotionType, Map<String, Double>> lexiconsPerEmotion = new HashMap<>(6);

    private Map<String, Double> getLexiconFor(final EmotionType emotionType) {
        return lexiconsPerEmotion.computeIfAbsent(emotionType, k -> new HashMap<>());
    }

    private EmoLex() {
        initializeLexiconsPerEmotion();
    }

    private void initializeLexiconsPerEmotion() {
        SCORES_FILENAME_PER_EMOTION.forEach((emotionType, filename) -> {
            try {
                final BufferedReader br = new BufferedReader(new InputStreamReader(EmoLex.class.getResourceAsStream("/" + filename)));
                String lineJustFetched;
                while ((lineJustFetched = br.readLine()) != null) {
                    final String[] tokens = lineJustFetched.split("\t");
                    if (tokens.length == 2) {
                        getLexiconFor(emotionType).put(tokens[0], Double.valueOf(tokens[1]));
                    }
                }
            } catch (Exception e) {
                // handle exception
                LOG.error("Exception while reading EmoLex lexicon: " + e.getLocalizedMessage());
                e.printStackTrace(System.err);
            }
        });
    }

    public double getEmotionScore(final EmotionType emotionType, final String word) {
        final Double lexiconScore = getLexiconFor(emotionType).get(word.toLowerCase());
        if (lexiconScore != null) {
            return lexiconScore;
        }
        return 0.0;
    }
}
