package com.o3.storyinspector.annotation.emotions.emolex;

import com.o3.storyinspector.storydom.constants.EmotionType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class implementing emotion analysis functions using the NRC Word-Emotion Association Lexicon (EmoLex).
 */
public class EmoLex {

    private static EmoLex singleton;

    public static synchronized EmoLex getInstance() {
        if (singleton == null) {
            singleton = new EmoLex();
        }
        return singleton;
    }

    private final Map<EmotionType, String> SCORES_FILENAME_PER_EMOTION =
            Map.of(EmotionType.ANGER, "emolex/anger-scores.txt",
                    EmotionType.ANTICIPATION, "emolex/anticipation-scores.txt",
                    EmotionType.DISGUST, "emolex/disgust-scores.txt",
                    EmotionType.FEAR, "emolex/fear-scores.txt",
                    EmotionType.SADNESS, "emolex/sadness-scores.txt",
                    EmotionType.SURPRISE, "emolex/surprise-scores.txt",
                    EmotionType.TRUST, "emolex/trust-scores.txt");

    private final Map<EmotionType, Map<String, Double>> lexiconsPerEmotion = new HashMap<>(6);

    private final Map<String, Double> getLexiconFor(final EmotionType emotionType) {
        Map<String, Double> lexicon = lexiconsPerEmotion.get(emotionType);
        if (lexicon == null) {
            lexicon = new HashMap<>();
            lexiconsPerEmotion.put(emotionType, lexicon);
        }
        return lexicon;
    }

    private EmoLex() {
        initializeLexiconsPerEmotion();
    }

    private void initializeLexiconsPerEmotion() {
        SCORES_FILENAME_PER_EMOTION.forEach((emotionType, filename) -> {
            final String filePath = EmoLex.class.getResource("/" + filename).getPath().replaceFirst("/", "");
            try {
                final FileInputStream fstream = new FileInputStream(filePath);
                final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String lineJustFetched;
                while ((lineJustFetched = br.readLine()) != null) {
                    final String[] tokens = lineJustFetched.split("\t");
                    if (tokens.length == 2) {
                        getLexiconFor(emotionType).put(tokens[0], Double.valueOf(tokens[1]));
                    }
                }
            } catch (Exception e) {
                // handle exception
                System.err.println("Exception while reading EmoLex file.");
                e.printStackTrace(System.err);
            }
        });
    }

    public double getEmotionScore(final EmotionType emotionType, final String word) {
        final Double lexiconScore = getLexiconFor(emotionType).get(word.toLowerCase());
        if (lexiconScore != null) {
            return lexiconScore.doubleValue();
        }
        return 0.0;
    }
}
