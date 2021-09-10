package com.o3.storyinspector.gui.reportarea.readability;

import com.o3.storyinspector.gui.core.domain.Block;
import com.o3.storyinspector.gui.core.domain.Blocks;
import com.o3.storyinspector.gui.core.domain.Sentence;
import com.o3.storyinspector.gui.utils.IconUtils;
import com.o3.storyinspector.storydom.Book;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import org.controlsfx.glyphfont.FontAwesome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import static javafx.application.Platform.runLater;

public class ReadabilityReportTab extends Tab {

    static final Logger LOGGER = LoggerFactory.getLogger(ReadabilityReportTab.class);

    private static String CSS =
            "body {\n" +
                    "  margin: 0;\n" +
                    "  font-family: \"Nunito\", -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif, \"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\", \"Noto Color Emoji\";\n" +
                    "  font-size:  0.75rem;\n" +
                    "  font-weight: 400;\n" +
                    "  line-height: 1.5;\n" +
                    "  color: #212529;\n" +
                    "  text-align: left;\n" +
                    "  background-color: #fff;\n" +
                "}\n" +
            ".complex-sentence {\n" +
                    "  background-color: #f18973;\n" +
                    "}";

    private static String PAGE_HEADER = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<style>\n" +
            CSS +
            "</style>\n" +
            "</head>\n" +
            "<body>" +
            "<div id='pagediv'>";

    private static String PAGE_TRAILER = "</div></body></html>";

    Book book;
    final List<WebView> pages = new ArrayList<>();

    public ReadabilityReportTab(final Book book) {
        super(book.getTitle() + " (Readability)");
        this.setGraphic(IconUtils.getIcon(FontAwesome.Glyph.PENCIL));
        this.book = book;

        final Task<Blocks> calculateReadability = new Task<>() {
            @Override
            protected Blocks call() {
                LOGGER.info("starting to process readability report");
                final Blocks blocks = Blocks.buildBlocks(book, book.getTitle(), book.getAuthor());
                LOGGER.info("finished processing readability report");
                return blocks;
            }
        };

        final ProgressIndicator progressIndicator = new ProgressIndicator();
//        progressIndicator.progressProperty().bind(calculateReadability.progressProperty());
        final Label statusLabel = new Label("Calculating readability scores...");
//        statusLabel.textProperty().bind(calculateReadability.messageProperty());
        final VBox loadingMessage = new VBox(5, statusLabel, progressIndicator);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(loadingMessage);
        StackPane.setAlignment(loadingMessage, Pos.TOP_LEFT);
        StackPane.setAlignment(loadingMessage, Pos.CENTER);
        this.setContent(loadingMessage);

        calculateReadability.setOnSucceeded(e -> {
            final Blocks blocks = calculateReadability.getValue();

            final VBox stackedTitledPanes = new VBox();
            final List<Block> blockList = blocks.getBlocks();
            blockList.sort((Comparator.comparingDouble(Block::getFkGrade).reversed()));
            final DoubleSummaryStatistics blockStats = blockList.stream()
                    .mapToDouble(Block::getFkGrade)
                    .summaryStatistics();
            blockStats.getAverage();

            for (final Block block : blockList) {
                LOGGER.info("Processing block #" + block.getId());
                double blockThreshold = Math.floor(block.getFkGrade());
                blockThreshold = (blockThreshold >= 8) ? blockThreshold : 8;
                LOGGER.info("Creating page");
                final WebView page = new WebView();
                pages.add(page);
                LOGGER.info("Added page");
                final StringBuilder pageBuilder = new StringBuilder();
                pageBuilder.append(PAGE_HEADER);
                for (final Sentence sentence : block.getSentences()) {
                    LOGGER.info("Processing sentence");
                    if (sentence.getFkGrade() > blockThreshold) {
                        pageBuilder.append("<span class=\"complex-sentence\">")
                                .append(sentence.getBody())
                                .append(" </span>");
                    } else {
                        pageBuilder.append(sentence.getBody());
                    }
                }
                pageBuilder.append(PAGE_TRAILER);
                LOGGER.info("loading HTML content");
                page.getEngine().loadContent(pageBuilder.toString());
//            blockText.setWrapText(true);
//            blockText.setPrefHeight(200);
//            blockText.setEditable(false);
                final VBox box = new VBox();
                final Label readabilityLabel = new Label("Readability: " + Blocks.getFkGradeMessage(block.getFkGrade()));
                box.getChildren().add(readabilityLabel);
                box.getChildren().add(page);
                final String tabTitle = "Page #" + block.getId() +
                        " (Chapter " + block.getChapter().getId() + ": " +
                        block.getChapter().getTitle() + ")";
                final TitledPane tab1 = new TitledPane(tabTitle, box);
                tab1.setExpanded(true);
                stackedTitledPanes.getChildren().add(tab1);
                LOGGER.info("Finished processing block #" + block.getId());
            }
            // scrollable content
            final ScrollPane scrollPane = new ScrollPane(stackedTitledPanes);
            scrollPane.fitToWidthProperty().set(true);

            this.setContent(scrollPane);
            listenToPageHeightChanges();
            adjustHeightOfPages();
        });

        new Thread(calculateReadability).start();
        LOGGER.info("Completed readability tab creation");
    }

    private void listenToPageHeightChanges() {
        LOGGER.info("listenToPageHeightChanges");
        for (WebView page : this.pages) {
            page.getEngine()
                    .getLoadWorker()
                    .stateProperty()
                    .addListener(
                            (ObservableValue<? extends Worker.State> arg0, Worker.State oldState, Worker.State newState) -> {
                                if (newState == Worker.State.SUCCEEDED) {
                                    adjustHeightOfPages();
                                }});
        }
    }

    private void adjustHeightOfPages() {
        runLater(() -> {
            LOGGER.info("adjustHeightOfPages");
            for (WebView page : this.pages) {
                try {
                    Object result = page.getEngine().executeScript(
                            "document.getElementById('pagediv').offsetHeight");
                    if (result instanceof Integer) {
                        final Integer i = (Integer) result;
                        double height = Double.valueOf(i);
                        height = height + 20;
                        page.setPrefHeight(height);
                    }
                } catch (JSException e) {
                    // not important
                }
            }
        });
    }

}
