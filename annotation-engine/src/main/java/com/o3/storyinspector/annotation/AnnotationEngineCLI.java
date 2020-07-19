package com.o3.storyinspector.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(
        name = "AnnotationEngine",
        description = "Reads an input storydom file and produces another storydom file as output with annotations."
)
public class AnnotationEngineCLI implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationEngineCLI.class);

    @CommandLine.Option(names = {"-I", "--input"}, required = true)
    private String inputPath;

    @CommandLine.Option(names = {"-O", "--output"}, required = true)
    private String outputPath;

    public static void main(String[] args) {
        CommandLine.run(new AnnotationEngineCLI(), args);
    }

    @Override
    public void run() {
        LOG.info("Preparing to annotate StoryDom, input=[" + inputPath + "], output=[" + outputPath + "]");
        try {
            AnnotationEngine.annotateBook(inputPath, outputPath);
        } catch (Exception e) {
            LOG.error("An error has occurred. Message: " + e.getLocalizedMessage());
        }
    }
}
