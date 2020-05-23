package com.o3.storyinspector.annotation.util;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class StanfordCoreNLPSingleton {

    private static StanfordCoreNLP singleton = null;

    private static void init() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        singleton = new StanfordCoreNLP(props);
    }

    public static synchronized StanfordCoreNLP getInstance() {
        if (singleton == null) {
            init();
        }
        return singleton;
    }

}
