package com.o3.storyinspector.viztool;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(
        name = "VizTool",
        description = "Reads an input annotated storydom file and produces an HTML report."
)
public class VizToolCLI implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(VizToolCLI.class);

    @CommandLine.Option(names = {"-I", "--input"}, required = true)
    private String inputPath;

    @CommandLine.Option(names = {"-O", "--output"}, required = true)
    private String outputPath;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        CommandLine.run(new VizToolCLI(), args);
    }

    @Override
    public void run() {
        LOG.info("Preparing to create report, input=[" + inputPath + "], output=[" + outputPath + "]");
        try {
            VizTool.storyDomToHtml(inputPath, outputPath);
        } catch (Exception e) {
            LOG.error("An error has occurred. Message: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
